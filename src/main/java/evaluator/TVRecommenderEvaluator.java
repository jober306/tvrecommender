package evaluator;

import static util.TVDataSetUtilities.filterByDateTime;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;

import recommender.SpaceAlignmentRecommender;
import recommender.TVRecommender;
import scala.Tuple2;
import util.IntHolder;
import data.EPG;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;

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
public class TVRecommenderEvaluator<T extends TVProgram, U extends TVEvent> {

	/**
	 * The electronic programming guide.
	 */
	EPG<T> epg;

	/**
	 * The whole data set.
	 */
	TVDataSet<U> tvDataSet;

	/**
	 * The start time and end time on which testing will be done. testSet will
	 * be created automatically from tv data set.
	 */
	LocalDateTime testStartTime;
	LocalDateTime testEndTime;
	TVDataSet<U> testSet;

	/**
	 * The tv recommender to evaluate.
	 */
	TVRecommender<T, U> recommender;

	/**
	 * The array of measures on which evaluation will be based.
	 */
	EvaluationMeasure[] measures;

	/**
	 * The map containing results for each evaluation measure.
	 */
	Map<EvaluationMeasure, Double> evaluationResults;

	/**
	 * Constructor of the tv recommender evaluator.
	 * 
	 * @param epg
	 *            The electronic programming guide. It must contains the
	 *            information over the testing time.
	 * @param tvDataSet
	 *            The tv data events from which the test set will be created.
	 * @param recommender
	 *            The tv recommender. It must be ready to make recommendations
	 *            (i.e it must have been trained if it needed to).
	 * @param measures
	 *            The evaluations measures that need to be computed.
	 * @param testStartTime
	 *            The starting time of the test period.
	 * @param testEndTime
	 *            The end time of the test period.
	 */
	public TVRecommenderEvaluator(EPG<T> epg, TVDataSet<U> tvDataSet,
			TVRecommender<T, U> recommender, EvaluationMeasure[] measures,
			LocalDateTime testStartTime, LocalDateTime testEndTime) {
		this.epg = epg;
		this.tvDataSet = tvDataSet;
		this.measures = measures;
		this.evaluationResults = new HashMap<EvaluationMeasure, Double>();
		this.recommender = recommender;
		this.testStartTime = testStartTime;
		this.testEndTime = testEndTime;
		this.testSet = buildTestSet();
	}

	private TVDataSet<U> buildTestSet() {
		JavaRDD<U> eventsOccuringDuringTestTime = filterByDateTime(
				tvDataSet.getEventsData(), testStartTime, testEndTime);
		return tvDataSet.newInstance(eventsOccuringDuringTestTime,
				tvDataSet.getJavaSparkContext());
	}

	/**
	 * Method that returns the results of the different given measures. Make
	 * sure to call evaluate before calling this method otherwise it will be
	 * empty.
	 * 
	 * @return A map containing the results for each given evaluation measure.
	 */
	public Map<EvaluationMeasure, Double> getResults() {
		return evaluationResults;
	}

	/**
	 * Method that evaluates all the evaluation measures given in measures. The
	 * results are stored in the evaluationResults map.
	 */
	public void evaluate() {
		for (EvaluationMeasure measure : measures) {
			switch (measure) {
			case MEAN_AVERAGE_PRECISION_AT_10:
				evaluationResults.put(
						EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_10,
						calculateMeanAveragePrecision(10));
				break;
			case MEAN_AVERAGE_PRECISION_AT_20:
				evaluationResults.put(
						EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_20,
						calculateMeanAveragePrecision(20));
				break;
			case MEAN_AVERAGE_PRECISION_AT_50:
				evaluationResults.put(
						EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_50,
						calculateMeanAveragePrecision(50));
				break;
			default:
				break;
			}
		}
	}

	private double calculateMeanAveragePrecision(int numberOfResults) {
		List<Integer> userIds = recommender.getTrainingSet().getAllUserIds();
		IntHolder numberOfActiveUser = new IntHolder();
		double meanAveragePrecision = 0.0d;
		System.out.println("Number of users to evaluate: " + userIds.size());
		for (int userId : userIds) {
			System.out.print("Evaluating user " + userId + "...");
			Date currentDate = new Date();
			meanAveragePrecision += calculateAveragePrecisionForUser(
					numberOfResults, meanAveragePrecision, numberOfActiveUser,
					userId);
			Date doneDate = new Date();
			System.out.println("Done!");
			System.out.println("It took: " + (currentDate.getTime() - doneDate.getTime()));
		}
		return meanAveragePrecision /= numberOfActiveUser.value;
	}

	private double calculateAveragePrecisionForUser(int numberOfResults,
			double meanAveragePrecision, IntHolder numberOfActiveUser,
			int userId) {
		List<Integer> groundTruth = testSet.getProgramIndexesSeenByUser(userId);
		if (groundTruth.size() > 0) {
			numberOfActiveUser.value += 1;
			List<Integer> recommendedTVShowIndexes = recommender.recommend(
					userId, testStartTime, testEndTime, numberOfResults);
			double averagePrecision = calculateAveragePrecision(
					numberOfResults, recommendedTVShowIndexes, groundTruth);
			meanAveragePrecision += averagePrecision;
		}
		return meanAveragePrecision;
	}

	private double calculateAveragePrecision(int numberOfResults,
			List<Integer> recommendedTVShowIndexes,
			List<Integer> actuallySeenTVShowIndexes) {
		double averagePrecision = 0.0d;
		double truePositiveRecommendedTVShow = 0;
		for (int k = 1; k <= Math.min(recommendedTVShowIndexes.size(),
				numberOfResults); k++) {
			int recommendedTVShowIndex = recommendedTVShowIndexes.get(k - 1);
			if (actuallySeenTVShowIndexes.contains(recommendedTVShowIndex)) {
				truePositiveRecommendedTVShow++;
				averagePrecision += (double) truePositiveRecommendedTVShow
						/ (double) k;
			}
		}
		if (truePositiveRecommendedTVShow != 0) {
			averagePrecision /= truePositiveRecommendedTVShow;
		}
		return averagePrecision;
	}

	public static void main(String[] args) {
		LocalDateTime trainingStartTime = RecsysTVDataSet.START_TIME;
		LocalDateTime trainingEndTime = RecsysTVDataSet.START_TIME.plusDays(7);
		LocalDateTime testStartTime = RecsysTVDataSet.START_TIME.plusDays(7);
		LocalDateTime testEndTime = RecsysTVDataSet.START_TIME.plusDays(8);
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader();
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader
				.loadDataSet(minDuration);
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		RecsysBooleanFeatureExtractor featureExtractor = new RecsysBooleanFeatureExtractor(
				epg);
		SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent> recommender = new SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent>(
				epg, events, trainingStartTime, trainingEndTime,
				featureExtractor, 100, 10);
		recommender.train();
		EvaluationMeasure[] measures = { EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_10 };
		TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent>(
				epg, events, recommender, measures, testStartTime, testEndTime);
		evaluator.evaluate();
		System.out.println(evaluator.getResults().get(
				EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_10));
	}
}
