package evaluator;

import static util.TVDataSetUtilities.filterByDateTime;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;

import recommender.TVRecommender;
import data.EPG;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;

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
public class TVRecommenderEvaluator<T extends TVProgram, U extends TVEvent, G extends TVRecommender<T, U>> {

	/**
	 * The electronic programming guide.
	 */
	EPG<T> epg;

	/**
	 * The training set on which the space alignment recommender will be
	 * trained.
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
	 * The space alignment recommender trained on training set.
	 */
	G recommender;

	/**
	 * The array of measures to evaluate.
	 */
	EvaluationMeasure[] measures;

	/**
	 * The map containing results for each evaluation measure in measures.
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
			G recommender, EvaluationMeasure[] measures,
			LocalDateTime testStartTime, LocalDateTime testEndTime) {
		this.epg = epg;
		this.tvDataSet = tvDataSet;
		this.measures = measures;
		this.evaluationResults = new HashMap<EvaluationMeasure, Double>();
		this.recommender = recommender;
		buildTestSet(testStartTime, testEndTime);
	}

	private void buildTestSet(LocalDateTime testStartTime,
			LocalDateTime testEndTime) {
		JavaRDD<U> eventsOccuringDuringTestTime = filterByDateTime(
				tvDataSet.getEventsData(), testStartTime, testEndTime);
		this.testSet = tvDataSet.newInstance(eventsOccuringDuringTestTime,
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
						evaluateMeanAveragePrecision(10));
				break;
			case MEAN_AVERAGE_PRECISION_AT_20:
				evaluationResults.put(
						EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_20,
						evaluateMeanAveragePrecision(20));
				break;
			case MEAN_AVERAGE_PRECISION_AT_50:
				evaluationResults.put(
						EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_50,
						evaluateMeanAveragePrecision(50));
				break;
			default:
				break;
			}
		}
	}

	private double evaluateMeanAveragePrecision(int numberOfResults) {
		double meanAveragePrecision = 0.0d;
		List<Integer> userdIds = recommender.getTrainingSet().getAllUserIds();
		int numberOfActiveUsers = 0;
		for (int userId : userdIds) {
			// TODO: Apply the same minimum time filter (maybe it is already
			// applied when loading the data).
			List<Integer> actuallySeenTVShowIndexes = testSet
					.getProgramIndexesSeenByUser(userId);
			if (actuallySeenTVShowIndexes.size() > 0) {
				numberOfActiveUsers++;
				List<Integer> recommendedTVShowIndexes = recommender.recommend(
						userId, testStartTime, testEndTime, numberOfResults);
				double averagePrecision = calculateAveragePrecision(
						numberOfResults, recommendedTVShowIndexes,
						actuallySeenTVShowIndexes);
				meanAveragePrecision += averagePrecision;
			}
		}
		return meanAveragePrecision /= numberOfActiveUsers;
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
}
