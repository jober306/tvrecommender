package evaluator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaSparkContext;

import data.EvaluationContext;
import data.TVEvent;
import data.TVProgram;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import evaluator.metric.EvaluationMetric;
import evaluator.metric.Precision;
import evaluator.metric.Recall;
import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import recommender.AbstractTVRecommender;
import recommender.channelpreference.ChannelPreferenceRecommender;
import recommender.channelpreference.TopChannelPerUserRecommender;
import scala.Tuple2;
import util.SparkUtilities;

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
public class TVRecommenderEvaluator<T extends TVProgram, U extends TVEvent, R extends AbstractRecommendation> {
	
	/**
	 * The evalaution context that was used to train the recommender and that will be used to test it.
	 */
	EvaluationContext<T, U> context;

	/**
	 * The tv recommender to evaluate.
	 */
	AbstractTVRecommender<T, U, R> recommender;

	/**
	 * The array of measures on which evaluation will be based.
	 */
	List<EvaluationMetric<R>> metrics;


	/**
	 * Constructor of a tv recommender evaluator. It assumes the recommender lives in an evaluation context.
	 * @param recommender The recommender to be evaluated.
	 * @param metrics The measures with respect to the recommender will be tested.
	 */
	public TVRecommenderEvaluator(AbstractTVRecommender<T, U, R> recommender, List<EvaluationMetric<R>> metrics) {
		this.context = (EvaluationContext<T,U>) recommender.getContext();
		this.metrics = metrics;
		this.recommender = recommender;
	}

	/**
	 * Method that evaluates the recommender on all test users. The
	 * results are stored in the evaluation metrics.
	 * @param numberOfRecommendations The number of recommendations the recommender need to output.
	 */
	public void evaluateAllUsers(int numberOfRecommendations) {
		List<Integer> testedUserIds = context.getTestSet().getAllUserIds();
		testedUserIds.stream().forEach(testUser -> evaluateUser(testUser, numberOfRecommendations));
	}
	
	/**
	 * Method that evaluate the recommender for a given user. The result is stored in the evaluation measures.
	 * @param userId The user to evaluate.
	 * @param numberOfRecommendations The number of recommendations the recommender need to output.
	 */
	public void evaluateUser(int userId, int numberOfRecommendations) {
		Recommendations<R> recommendations = recommender.recommend(userId, numberOfRecommendations, context.getTestPrograms());
		metrics.stream().forEach(metric -> metric.evaluate(recommendations, context));
	}
	
	public void printResults() {
		for(EvaluationMetric<R> metric : metrics) {
			System.out.println(metric + ": " + metric.mean());
		}
	}

	public static void main(String[] args) {
		LocalDateTime trainingStartTime = RecsysTVDataSet.START_TIME;
		LocalDateTime trainingEndTime = RecsysTVDataSet.START_TIME.plusDays(21);
		LocalDateTime testStartTime = RecsysTVDataSet.START_TIME.plusDays(21);
		LocalDateTime testEndTime = RecsysTVDataSet.START_TIME.plusDays(22);
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		EvaluationContext<RecsysTVProgram, RecsysTVEvent> context = new EvaluationContext<>(epg, events, trainingStartTime, trainingEndTime, testStartTime, testEndTime);
		List<EvaluationMetric<Recommendation>> measures = Arrays.asList(new Recall(2), new Precision(2), new Recall(5), new Precision(5), new Recall(10), new Precision(10));
		ChannelPreferenceRecommender recommender = new TopChannelPerUserRecommender(context);
		recommender.train();
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		evaluator.evaluateAllUsers(10);
		evaluator.printResults();
	}
}
