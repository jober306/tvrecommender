package evaluator;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaSparkContext;

import com.google.common.collect.Sets;

import data.EPG;
import data.EvaluationContext;
import data.TVDataSet;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import evaluator.metric.EvaluationMetric;
import evaluator.metric.Precision;
import evaluator.metric.Recall;
import evaluator.result.EvaluationInfo;
import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import model.recommendation.ScoredRecommendation;
import recommender.SpaceAlignmentRecommender;
import recommender.TVRecommender;
import recommender.channelpreference.ChannelPreferenceRecommender;
import recommender.channelpreference.TopChannelPerUserPerSlotRecommender;
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
 * @param <P>
 *            A class extending TVEvent on which the data set and the tv
 *            recommender is built.
 * @param <E>
 *            A class extending TVProgram on which the data set and the tv
 *            recommender is built.
 * @param <R>
 * 			  The type of recommendations made by the recommender.
 */
public class TVRecommenderEvaluator<U extends User, P extends TVProgram, E extends TVEvent<U, P>, R extends Recommendation> {

	/**
	 * The tv recommender to evaluate.
	 */
	final TVRecommender<U, P, E, R> recommender;

	/**
	 * The array of measures on which evaluation will be based.
	 */
	final Set<? extends EvaluationMetric<? super R>> metrics;
	
	/**
	 * A boolean indicating if the evaluation info must be generated. Can take some times.
	 */
	boolean evaluationInfoGenerated;

	public boolean isEvaluationInfoGenerated() {
		return evaluationInfoGenerated;
	}

	public void setEvaluationInfoGenerated(boolean EvaluationInfoGenerated) {
		this.evaluationInfoGenerated = EvaluationInfoGenerated;
	}

	/**
	 * Constructor of a tv recommender evaluator. The generation of evaluation information is set to false.
	 * @param recommender The recommender to be evaluated.
	 * @param metrics The measures with respect to the recommender will be tested.
	 */
	public TVRecommenderEvaluator(TVRecommender<U, P, E, R> recommender, Set<? extends EvaluationMetric<? super R>> metrics) {
		this.metrics = metrics;
		this.recommender = recommender;
		this.evaluationInfoGenerated = false;
	}
	
	public TVRecommenderEvaluator(TVRecommender<U, P, E, R> recommender, Set<EvaluationMetric<? super R>> metrics, boolean evaluationInfoGenerated){
		this.metrics = metrics;
		this.recommender = recommender;
		this.evaluationInfoGenerated = evaluationInfoGenerated;
	}
	
	public Set<EvaluationResult> evaluateWithIncreasingTrainingSize(EPG<P> epg, TVDataSet<U, P, E> events, LocalDateTime startTime, LocalDateTime endTime){
		DateTimeRange trainingStartTimes = new DateTimeRange(startTime, startTime, Duration.ZERO);
		DateTimeRange trainingEndTimes = new DateTimeRange(startTime.plusDays(1), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(startTime.plusDays(1), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(startTime.plusDays(2), endTime.minusDays(1), Duration.ofDays(1));
		return evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
	}
	
	public Set<EvaluationResult> evaluateMovingTimeWindow(EPG<P> epg, TVDataSet<U, P, E> events, LocalDateTime startTime, Period window, LocalDateTime endTime){
		DateTimeRange trainingStartTimes = new DateTimeRange(startTime, endTime.minus(window).minusDays(1), Duration.ofDays(1));
		DateTimeRange trainingEndTimes = new DateTimeRange(startTime.plus(window), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(startTime.plus(window), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(startTime.plus(window).plusDays(1), endTime, Duration.ofDays(1));
		return evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
	}
	
	public Set<EvaluationResult> evaluateTimeSeries(EPG<P> epg, TVDataSet<U, P, E> events, DateTimeRange trainingStartTimes, DateTimeRange trainingEndTimes, DateTimeRange testingStartTimes, DateTimeRange testingEndTimes){
		return StreamUtilities.zip(trainingStartTimes.stream(), trainingEndTimes.stream(), testingStartTimes.stream(), testingEndTimes.stream())
			.map(dateTimes -> {
				System.out.print("Creating evaluation context...");
				Instant evalContextStartTime = Instant.now();
				EvaluationContext<U, P, E> evalContext =  new EvaluationContext<>(epg, events, dateTimes);
				Instant evalContextEndTime = Instant.now();
				System.out.println("Done in " + SECONDS.between(evalContextStartTime, evalContextEndTime) + " seconds!");
				return evalContext;
			})
			.map(this::evaluate)
			.collect(Collectors.toSet());
	}
	
	public EvaluationResult evaluate(EvaluationContext<U, P, E> context){
		System.out.print("Setting context...");
		Instant settingStartTime = Instant.now();
		recommender.setContext(context);
		Instant settingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(settingStartTime, settingEndTime) + " seconds!");
		System.out.print("Training recommender...");
		Instant trainingStartTime = Instant.now();
		recommender.train();
		Instant trainingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(trainingStartTime, trainingEndTime) + " seconds!");
		EvaluationResult result = evaluate();
		return result;
	}
	
	/**
	 * Method that evaluates the recommender on all test users.
	 * @param numberOfRecommendations The number of recommendations the recommender need to output.
	 */
	public EvaluationResult evaluate() {
		EvaluationContext<? extends U, ? extends P, ? extends E> context = (EvaluationContext<? extends U, ? extends P, ? extends E>) recommender.getContext();
		Instant recommendingStartTime = Instant.now();
		System.out.print("Recommending for tested users...");
		List<Recommendations<? extends U, R>> testedUserRecommendations = context.getTestSet().getAllUsers().stream()
			.map(testedUser -> recommender.recommend(testedUser, context.getTestPrograms()))
			.collect(Collectors.toList());
		Instant recommendingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(recommendingStartTime, recommendingEndTime) + " seconds!");
		Instant metricStartTime = Instant.now();
		System.out.print("Evaluating metrics...");
		List<MetricResults> results = metrics.stream().map(metric -> {
				Map<Integer, Double> userScores = StreamUtilities.toMapAverage(testedUserRecommendations.stream(), Recommendations::userId, recommendation -> metric.evaluate(recommendation, context));
				return new MetricResults(metric.name(), userScores);
			}
			).collect(Collectors.toList());
		Instant metricEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(metricStartTime, metricEndTime) + " seconds!");
		Instant infoStartTime = Instant.now();
		EvaluationInfo evaluationInfo = null;
		if(isEvaluationInfoGenerated()){
			System.out.print("Creating evaluation info...");
			evaluationInfo = new EvaluationInfo(recommender, (EvaluationContext<? extends U, ? extends P, ? extends E>)recommender.getContext());
			Instant infoEndTime = Instant.now();
			System.out.println("Done in " + SECONDS.between(infoStartTime, infoEndTime) + " seconds!");
		}
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
		EvaluationContext<User, RecsysTVProgram, RecsysTVEvent> evalContext = new EvaluationContext<>(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
		Set<EvaluationMetric<Recommendation>> measures = Sets.newHashSet(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		ChannelPreferenceRecommender recommender = new TopChannelRecommender(evalContext, 10);
		recommender.train();
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		EvaluationResult result = evaluator.evaluate();
		result.toFile("src/main/resources/" + result.generateFileName());
	}
	
	public static void evaluateTimeWindow(){
		LocalDateTime startTime = RecsysTVDataSet.START_TIME;
		Period window = Period.ofWeeks(1);
		LocalDateTime endTime = RecsysTVDataSet.START_TIME.plusMonths(1);
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		System.out.print("Loading data...");
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		System.out.println("Done!");
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, ScoredRecommendation> evaluator = spaceAlignementEvaluator(sc, epg);
		Set<EvaluationResult> results = evaluator.evaluateMovingTimeWindow(epg, events, startTime, window, endTime);
		Stream<MetricResults> metricResults = results.stream().map(EvaluationResult::metricsResults).flatMap(List::stream);
		Map<String, Double> averagePerMetric = StreamUtilities.toMapAverage(metricResults, MetricResults::metricName, MetricResults::mean);
		System.out.println(averagePerMetric.entrySet().stream().map(Entry::toString).collect(Collectors.joining(", ", "[", "]")));
//		final String outputDir = "src/main/resources/results/topchannel/onemonth/";
//		EvaluationVisualisator.plotTimeSeries(results, outputDir);
//		results.stream().forEach(result -> result.serialize(outputDir + result.generateFileName() + ".ser"));
		sc.close();
	}

	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, ScoredRecommendation> spaceAlignementEvaluator(JavaSparkContext sc, RecsysEPG epg) {
		Set<EvaluationMetric<Recommendation>> measures = Sets.newHashSet(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		int rank = 50;
		int neighbourhoodSize = 10;
		SpaceAlignmentRecommender<User, RecsysTVProgram, RecsysTVEvent> recommender = new SpaceAlignmentRecommender<>(10, new RecsysBooleanFeatureExtractor(epg), rank, neighbourhoodSize, sc);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, ScoredRecommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> topChannelEvaluator() {
		Set<EvaluationMetric<Recommendation>> measures = Sets.newHashSet(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		ChannelPreferenceRecommender recommender = new TopChannelRecommender(10);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> topChannelPerUserEvaluator() {
		Set<EvaluationMetric<Recommendation>> measures = Sets.newHashSet(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		ChannelPreferenceRecommender recommender = new TopChannelPerUserRecommender(10);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> topChannelPerUserPerSlotEvaluator() {
		Set<EvaluationMetric<Recommendation>> measures = Sets.newHashSet(new Recall(2), new Recall(5), new Recall(10), new Precision(2), new Precision(5), new Precision(10));
		ChannelPreferenceRecommender recommender = new TopChannelPerUserPerSlotRecommender(10);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		return evaluator;
	}
}
