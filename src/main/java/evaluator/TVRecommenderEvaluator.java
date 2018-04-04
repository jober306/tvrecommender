package evaluator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaSparkContext;

import data.EvaluationContext;
import data.TVEvent;
import data.TVProgram;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import evaluator.information.Informative;
import evaluator.metric.EvaluationMetric;
import evaluator.metric.Precision;
import evaluator.metric.Recall;
import evaluator.result.EvaluationInfo;
import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import recommender.AbstractTVRecommender;
import recommender.channelpreference.ChannelPreferenceRecommender;
import recommender.channelpreference.TopChannelPerUserRecommender;
import scala.Tuple2;
import util.spark.SparkUtilities;

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
public class TVRecommenderEvaluator<T extends TVProgram, U extends TVEvent, R extends AbstractRecommendation> implements Informative{
	
	/**
	 * The evalaution context that was used to train the recommender and that will be used to test it.
	 */
	final EvaluationContext<T, U> context;

	/**
	 * The tv recommender to evaluate.
	 */
	final AbstractTVRecommender<T, U, R> recommender;

	/**
	 * The array of measures on which evaluation will be based.
	 */
	final List<EvaluationMetric<R>> metrics;
	
	final EvaluationInfo info;


	/**
	 * Constructor of a tv recommender evaluator. It assumes the recommender lives in an evaluation context.
	 * @param recommender The recommender to be evaluated.
	 * @param metrics The measures with respect to the recommender will be tested.
	 */
	public TVRecommenderEvaluator(AbstractTVRecommender<T, U, R> recommender, List<EvaluationMetric<R>> metrics) {
		this.context = (EvaluationContext<T,U>) recommender.getContext();
		this.metrics = metrics;
		this.recommender = recommender;
		this.info = new EvaluationInfo(recommender, context);
	}

	/**
	 * Method that evaluates the recommender on all test users.
	 * @param numberOfRecommendations The number of recommendations the recommender need to output.
	 */
	public EvaluationResult evaluate() {
		List<Recommendations<R>> testedUserRecommendations = context.getTestSet().getAllUserIds().stream()
			.map(testedUser -> recommender.recommend(testedUser, context.getTestPrograms()))
			.collect(Collectors.toList());
		Map<String, MetricResults> results = metrics.stream().collect(Collectors.toMap(EvaluationMetric::name, metric -> metric.evaluate(testedUserRecommendations.stream(), context)));
		return new EvaluationResult(results, info);
	}
	
	@Override
	public EvaluationInfo info() {
		return this.info;
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
		ChannelPreferenceRecommender recommender = new TopChannelPerUserRecommender(context, 10);
		recommender.train();
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent, Recommendation> evaluator = new TVRecommenderEvaluator<>(recommender, measures);
		evaluator.evaluate();
	}
}
