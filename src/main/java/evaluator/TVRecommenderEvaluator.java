package evaluator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaSparkContext;

import data.EPG;
import data.EvaluationContext;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.visualisation.TVDataSetVisualisation;
import evaluator.metric.EvaluationMetric;
import evaluator.metric.Precision;
import evaluator.metric.Recall;
import evaluator.result.EvaluationInfo;
import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import evaluator.visualisation.EvaluationVisualisator;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import model.recommendation.ScoredRecommendation;
import recommender.TVRecommender;
import recommender.SpaceAlignmentRecommender;
import recommender.channelpreference.ChannelPreferenceRecommender;
import recommender.channelpreference.TopChannelPerUserRecommender;
import recommender.channelpreference.TopChannelRecommender;
import scala.Tuple2;
import util.collections.StreamUtilities;
import util.spark.SparkUtilities;
import util.time.DateTimeRange;

/**
 * Class that evaluate a tv recommender on a given data set.
 * 
 * @author Jonathan Bergeron
 *
 * @param <T>
 *            A class extending TVEvent on which the data set and the tv
 *            recommender is built.
 * @param <U>
 *            A class extending TVProgram on which the data set and the tv
 *            recommender is built.
 */
public class TVRecommenderEvaluator<T extends TVProgram, U extends TVEvent, R extends Recommendation> {

	/**
	 * The tv recommender to evaluate.
	 */
	final TVRecommender<T, U, R> recommender;

	/**
	 * The array of measures on which evaluation will be based.
	 */
	final List<EvaluationMetric<? super R>> metrics;


	/**
	 * Constructor of a tv recommender evaluator. It assumes the recommender lives in an evaluation context.
	 * @param recommender The recommender to be evaluated.
	 * @param metrics The measures with respect to the recommender will be tested.
	 */
	public TVRecommenderEvaluator(TVRecommender<T, U, R> recommender, List<EvaluationMetric<? super R>> metrics) {
		this.metrics = metrics;
		this.recommender = recommender;
	}
	
	public Set<EvaluationResult> evaluateTimeSeries(EPG<T> epg, TVDataSet<T, U> events, DateTimeRange trainingStartTime, DateTimeRange trainingEndTime, DateTimeRange testingStartTime, DateTimeRange testingEndTime){
		return StreamUtilities.zip(trainingStartTime.stream(), trainingEndTime.stream(), testingStartTime.stream(), testingEndTime.stream())
			.map(dateTimes -> new EvaluationContext<T,U>(epg, events, dateTimes))
			.map(this::evaluateAndCloseContext)
			.collect(Collectors.toSet());
	}
	
	public EvaluationResult evaluate(EvaluationContext<T, U> context){
		recommender.setContext(context);
		recommender.train();
		return evaluate();
	}
	
	public EvaluationResult evaluateAndCloseContext(EvaluationContext<T, U> context){
		recommender.setContext(context);
		recommender.train();
		EvaluationResult result = evaluate();
		recommender.closeContextDatasets();
		return result;
	}
	
	/**
	 * Method that evaluates the recommender on all test users.
	 * @param numberOfRecommendations The number of recommendations the recommender need to output.
	 */
	public EvaluationResult evaluate() {
		EvaluationContext<T,U> context = (EvaluationContext<T,U>) recommender.getContext();
		System.out.print("Plotting test set...");
		TVDataSetVisualisation.createAndSaveSortedProgramCountChart(context.getTestSet(), "Testset.jpeg");
		System.out.println("Done!");
		List<Recommendations<R>> testedUserRecommendations = context.getTestSet().getAllUserIds().stream()
			.map(testedUser -> recommender.recommend(testedUser, context.getTestPrograms()))
			.collect(Collectors.toList());
		List<MetricResults> results = metrics.stream().map(metric -> {
				Map<Integer, Double> userScores = StreamUtilities.toMapAverage(testedUserRecommendations.stream(), Recommendations::userId, recommendation -> metric.evaluate(recommendation, context));
				return new MetricResults(metric.name(), userScores);
			}
			).collect(Collectors.toList());
		EvaluationInfo evaluationInfo = new EvaluationInfo(recommender, (EvaluationContext<T,U>)recommender.getContext());
		return new EvaluationResult(results, evaluationInfo);
	}

	public static void main(String[] args) {
		evaluateTimeWindow();
	}
	
	public static void evaluateSingle(){
		LocalDateTime trainingStartTimes = RecsysTVDataSet.START_TIME;
		LocalDateTime trainingEndTimes = RecsysTVDataSet.START_TIME.plusDays(7);
		LocalDateTime testingStartTimes = RecsysTVDataSet.START_TIME.plusDays(7);
		LocalDateTime testingEndTimes = RecsysTVDataSet.START_TIME.plusDays(8);
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		EvaluationContext<RecsysTVProgram, RecsysTVEvent> evalContext = new EvaluationContext<>(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
		List<EvaluationMetric<? super Recommendation>> measures = Arrays.asList(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		ChannelPreferenceRecommender recommender = new TopChannelPerUserRecommender(evalContext, 10);
		recommender.train();
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		EvaluationResult result = evaluator.evaluate();
		result.toFile("src/main/resources/" + result.generateFileName());
	}
	
	public static void evaluateTimeWindow(){
		DateTimeRange trainingStartTimes = new DateTimeRange(RecsysTVDataSet.START_TIME, RecsysTVDataSet.START_TIME, Duration.ZERO);
		DateTimeRange trainingEndTimes = new DateTimeRange(RecsysTVDataSet.START_TIME.plusDays(1), RecsysTVDataSet.START_TIME.plusDays(30), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(RecsysTVDataSet.START_TIME.plusDays(1), RecsysTVDataSet.START_TIME.plusDays(30), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(RecsysTVDataSet.START_TIME.plusDays(2), RecsysTVDataSet.START_TIME.plusDays(31), Duration.ofDays(1));
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = topChannelEvaluator();
		Set<EvaluationResult> results = evaluator.evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
		final String outputDir = "src/main/resources/results/topchannel/onemonth/";
		EvaluationVisualisator.plotTimeSeries(results, outputDir);
		results.stream().forEach(result -> result.serialize(outputDir + result.generateFileName() + ".ser"));
		sc.close();
	}

	private static TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, ScoredRecommendation> spaceAlignementEvaluator(
			JavaSparkContext sc, RecsysEPG epg) {
		List<EvaluationMetric<? super ScoredRecommendation>> measures = Arrays.asList(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		int rank = 5;
		int neighbourhoodSize = 50;
		SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent> recommender = new SpaceAlignmentRecommender<>(10, new RecsysBooleanFeatureExtractor(epg), rank, neighbourhoodSize, sc);
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, ScoredRecommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, Recommendation> topChannelEvaluator() {
		List<EvaluationMetric<? super Recommendation>> measures = Arrays.asList(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		TopChannelRecommender recommender = new TopChannelRecommender(10);
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		return evaluator;
	}
}
