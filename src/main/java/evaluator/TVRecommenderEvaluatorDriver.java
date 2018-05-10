package evaluator;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaSparkContext;

import data.EvaluationContext;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.utility.RecsysUtilities;
import evaluator.metric.EvaluationMetric;
import evaluator.metric.Novelty;
import evaluator.metric.Precision;
import evaluator.metric.Recall;
import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import model.data.User;
import recommender.SpaceAlignmentRecommender;
import recommender.channelpreference.ChannelPreferenceRecommender;
import recommender.channelpreference.TopChannelPerUserPerSlotRecommender;
import recommender.channelpreference.TopChannelPerUserRecommender;
import recommender.channelpreference.TopChannelRecommender;
import scala.Tuple2;
import util.collections.StreamUtilities;
import util.spark.SparkUtilities;

public class TVRecommenderEvaluatorDriver {
	
	public static void main(String[] args) {
		evaluateTimeWindow();
	}
	
	public static void evaluateSingle(){
		LocalDateTime trainingStartTimes = RecsysUtilities.START_TIME;
		LocalDateTime trainingEndTimes = RecsysUtilities.START_TIME.plusDays(7);
		LocalDateTime testingStartTimes = RecsysUtilities.START_TIME.plusDays(7);
		LocalDateTime testingEndTimes = RecsysUtilities.START_TIME.plusDays(8);
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		System.out.print("Loading data...");
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		System.out.println("Done!");
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		EvaluationContext<User, RecsysTVProgram, RecsysTVEvent> evalContext = new EvaluationContext<>(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
		ChannelPreferenceRecommender recommender = new TopChannelRecommender(evalContext, 10);
		recommender.train();
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<>(recommender, getMetrics());
		EvaluationResult result = evaluator.evaluate();
		result.toFile("src/main/resources/" + result.generateFileName());
	}
	
	public static void evaluateTimeWindow(){
		LocalDateTime startTime = RecsysUtilities.START_TIME;
		Period window = Period.ofWeeks(1);
		LocalDateTime endTime = RecsysUtilities.START_TIME.plusMonths(1);
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		System.out.print("Loading data...");
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		System.out.println("Done!");
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> evaluator = topChannelEvaluator();
		Set<EvaluationResult> results = evaluator.evaluateMovingTimeWindow(epg, events, startTime, window, endTime);
		Stream<MetricResults> metricResults = results.stream().map(EvaluationResult::metricsResults).flatMap(List::stream);
		Map<String, Double> averagePerMetric = StreamUtilities.toMapAverage(metricResults, MetricResults::metricName, MetricResults::mean);
		System.out.println(averagePerMetric.entrySet().stream().map(Entry::toString).collect(Collectors.joining(", ", "[", "]")));
//		final String outputDir = "src/main/resources/results/topchannel/onemonth/";
//		EvaluationVisualisator.plotTimeSeries(results, outputDir);
//		results.stream().forEach(result -> result.serialize(outputDir + result.generateFileName() + ".ser"));
		sc.close();
	}

	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> spaceAlignementEvaluator(JavaSparkContext sc, RecsysTVDataSet dataSet) {
		int rank = 50;
		int neighbourhoodSize = 10;
		SpaceAlignmentRecommender<User, RecsysTVProgram, RecsysTVEvent> recommender = new SpaceAlignmentRecommender<>(10, RecsysBooleanFeatureExtractor.instance(), rank, neighbourhoodSize, sc);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<>(recommender, getMetrics());
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> topChannelEvaluator() {
		ChannelPreferenceRecommender recommender = new TopChannelRecommender(10);
		Set<EvaluationMetric<User, RecsysTVProgram>> metrics = getMetrics();
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<>(recommender, metrics);
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> topChannelPerUserEvaluator() {
		ChannelPreferenceRecommender recommender = new TopChannelPerUserRecommender(10);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<>(recommender, getMetrics());
		return evaluator;
	}
	
	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> topChannelPerUserPerSlotEvaluator() {
		ChannelPreferenceRecommender recommender = new TopChannelPerUserPerSlotRecommender(10);
		TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<>(recommender, getMetrics());
		return evaluator;
	}
	
	private static Set<EvaluationMetric<User, RecsysTVProgram>> getMetrics() {
		Set<EvaluationMetric<User, RecsysTVProgram>> metrics = new HashSet<>();
		metrics.add(new Novelty<>());
		return metrics;
	}
}