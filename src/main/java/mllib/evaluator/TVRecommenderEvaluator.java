package mllib.evaluator;

import static list.utility.ListUtilities.intersection;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mllib.recommender.TVRecommender;
import data.feature.FeatureExtractor;
import data.model.EPG;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

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
	 * The space alignment recommender trained on training set.
	 */
	G recommender;

	/**
	 * Feature extractor used for this date set and epg programs.
	 */
	FeatureExtractor<T, U> extractor;

	/**
	 * The array of measures to evaluate.
	 */
	EvaluationMeasure[] measures;

	/**
	 * The map containing results for each evaluation measure in measures.
	 */
	Map<EvaluationMeasure, Double> evaluationResults;

	/**
	 * Time attributes that represents the interval of time that will be used to
	 * train the recommender.
	 */
	LocalDateTime trainingStartTime;
	LocalDateTime trainingEndTime;

	/**
	 * Time attributes that represents the interval of time that will be used to
	 * test the recommender.
	 */
	LocalDateTime testStartTime;
	LocalDateTime testEndTime;

	/**
	 * Constructor of the SpaceAlignmentEvaluator.
	 * 
	 * @param tvDataSet
	 *            A data set of tv events with more than just a week.
	 * @param measures
	 *            The array of evaluation measures that will be calculated.
	 * @param week
	 *            The week on which the training will be made.
	 * @param r
	 *            The rank constraint needed by the space alignment recommender.
	 */
	public TVRecommenderEvaluator(EPG<T> epg, TVDataSet<U> tvDataSet,
			TVRecommender<T, U> recommender, FeatureExtractor<T, U> extractor,
			EvaluationMeasure[] measures, LocalDateTime trainingStartTime,
			LocalDateTime trainingEndTime, LocalDateTime testStartTime,
			LocalDateTime testEndTime) {
		this.epg = epg;
		this.tvDataSet = tvDataSet;
		this.extractor = extractor;
		this.measures = measures;
		this.evaluationResults = new HashMap<EvaluationMeasure, Double>();
		this.trainingStartTime = trainingStartTime;
		this.trainingEndTime = trainingEndTime;
		this.testStartTime = testStartTime;
		this.testEndTime = testEndTime;
	}

	/**
	 * Method that evaluates all the evaluation measures given in measures. The
	 * results are stored in the evaluationResults map.
	 */
	public void evaluate() {
		for (EvaluationMeasure measure : measures) {
			switch (measure) {
			case MEAN_AVERAGE_PRECISION_AT_10:
				evaluateMeanAveragePrecision(10);
				break;
			case MEAN_AVERAGE_PRECISION_AT_20:
				evaluateMeanAveragePrecision(20);
				break;
			case MEAN_AVERAGE_PRECISION_AT_50:
				evaluateMeanAveragePrecision(50);
				break;
			case MEAN_AVERAGE_RECALL_AT_10:
				evaluateMeanAverageRecall(10);
				break;
			case MEAN_AVERAGE_RECALL_AT_20:
				evaluateMeanAverageRecall(20);
				break;
			case MEAN_AVERAGE_RECALL_AT_50:
				evaluateMeanAverageRecall(50);
				break;
			default:
				break;
			}
		}
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

	private void evaluateMeanAveragePrecision(int numberOfResults) {
		List<Integer> userdIds = recommender.getTrainingSet().getAllUserIds();
		double meanAveragePrecision = 0.0d;
		for (int userId : userdIds) {
			List<Integer> recommendedItemIndexes = recommender.recommend(
					userId, testStartTime, testEndTime, numberOfResults);
			System.out.println("Done");
			double averagePrecision = 0.0d;
			double recommendedItemSize = (double) recommendedItemIndexes.size();
			for (int n = 1; n < 10; n++) {
				if (originalIdsOfItemsSeenByUser
						.contains(recommendedItemIndexes.get(n))) {
					double intersectionSize = (double) intersection(
							originalIdsOfRecommendedItemIndexes,
							originalIdsOfItemsSeenByUser).size();
					averagePrecision += intersectionSize / recommendedItemSize;
				}
			}
			averagePrecision /= recommendedItemSize;
			System.out.println("Mean Average for user: " + userIndex + "/"
					+ numberOfUsers + " is " + averagePrecision);
			meanAveragePrecision += averagePrecision;
		}
		meanAveragePrecision /= (double) numberOfUsers;
		evaluationResults.put(EvaluationMeasure.MEAN_AVERAGE_PRECISION,
				meanAveragePrecision);
	}

	private void evaluateMeanAverageRecall(int numberOfResults) {

	}
}
