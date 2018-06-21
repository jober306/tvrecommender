package evaluator;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

import org.apache.spark.api.java.JavaSparkContext;

import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.utility.RecsysUtilities;
import evaluator.metric.AveragePrecision;
import evaluator.metric.Diversity;
import evaluator.metric.EvaluationMetric;
import evaluator.metric.Novelty;
import evaluator.metric.Recall;
import evaluator.result.EvaluationResult;
import model.data.User;
import model.measure.distance.CosineDistance;
import recommender.SpaceAlignmentRecommender;
import recommender.channelpreference.ChannelPreferenceRecommender;
import recommender.channelpreference.TopChannelPerUserPerSlotRecommender;
import recommender.channelpreference.TopChannelPerUserRecommender;
import recommender.channelpreference.TopChannelRecommender;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class TVRecommenderEvaluatorDriver {
	
	public static void main(String[] args) {
		//The moving time window configuration
		LocalDateTime startTime = RecsysUtilities.START_TIME;
		Period window = Period.ofWeeks(1);
		LocalDateTime endTime = RecsysUtilities.START_TIME.plusMonths(1);
		
		//Loading the data.
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		//caching data set
		data._2().cache();
		
		//******************MOVING TIME WINDOW EVALUATION***************************
		
		//Evaluating the top channel recommender
		Set<EvaluationResult<User, RecsysTVProgram>> topChannelResults = topChannelEvaluator().evaluateMovingTimeWindow(data._1(), data._2(), startTime, window, endTime);
		serializeEvaluationResults("src/main/resources/results/topchannel/window/", topChannelResults);
		
		//Evaluating the top channel per user recommender
		Set<EvaluationResult<User, RecsysTVProgram>> topChannelPerUserResults = topChannelPerUserEvaluator().evaluateMovingTimeWindow(data._1(), data._2(), startTime, window, endTime);
		serializeEvaluationResults("src/main/resources/results/topchannelperuser/window/", topChannelPerUserResults);
		
		//Evaluating the top channel per user per slot recommender
		Set<EvaluationResult<User, RecsysTVProgram>> topChannelPerUserPerSlotResults = topChannelPerUserPerSlotEvaluator().evaluateMovingTimeWindow(data._1(), data._2(), startTime, window, endTime);
		serializeEvaluationResults("src/main/resources/results/topchannelperuserperslot/window/", topChannelPerUserPerSlotResults);
		
		//Evaluating the space alignment recommender
		Set<EvaluationResult<User, RecsysTVProgram>> spaceAlignmentResults = spaceAlignementEvaluator(sc).evaluateMovingTimeWindow(data._1(), data._2(), startTime, window, endTime);
		serializeEvaluationResults("src/main/resources/results/spacealignment/window/", spaceAlignmentResults);
		
		//******************INCREASING TRAINING SET EVALUATION***************************

		//Evaluating the top channel recommender
		topChannelResults = topChannelEvaluator().evaluateWithIncreasingTrainingSize(data._1(), data._2(), startTime, endTime);
		serializeEvaluationResults("src/main/resources/results/topchannel/onemonth/", topChannelResults);
		
		//Evaluating the top channel per user recommender
		topChannelPerUserResults = topChannelPerUserEvaluator().evaluateWithIncreasingTrainingSize(data._1(), data._2(), startTime, endTime);
		serializeEvaluationResults("src/main/resources/results/topchannelperuser/onemonth/", topChannelPerUserResults);
		
		//Evaluating the top channel per user per slot recommender
		topChannelPerUserPerSlotResults = topChannelPerUserPerSlotEvaluator().evaluateWithIncreasingTrainingSize(data._1(), data._2(), startTime, endTime);
		serializeEvaluationResults("src/main/resources/results/topchannelperuserperslot/onemonth/", topChannelPerUserPerSlotResults);
		
		//Evaluating the space alignment recommender
		spaceAlignmentResults = spaceAlignementEvaluator(sc).evaluateWithIncreasingTrainingSize(data._1(), data._2(), startTime, endTime);
		serializeEvaluationResults("src/main/resources/results/spacealignment/onemonth/", spaceAlignmentResults);
		
		sc.close();
	}

	private static TVRecommenderEvaluator<User, RecsysTVProgram, RecsysTVEvent> spaceAlignementEvaluator(JavaSparkContext sc) {
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
		metrics.add(new Diversity<>(RecsysBooleanFeatureExtractor.instance(), CosineDistance.instance()));
		metrics.add(new Recall<>(10));
		metrics.add(new AveragePrecision<>(10));
		return metrics;
	}
	
	private static void serializeEvaluationResults(String outputDir, Set<? extends EvaluationResult<?, ?>> results) {
		results.stream().forEach(result -> result.serialize(outputDir + result.generateFileName() + ".ser"));
	}
}
