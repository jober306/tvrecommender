package evaluator;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;

import recommender.AbstractTVRecommender;
import recommender.SpaceAlignmentRecommender;
import scala.Tuple2;
import util.SparkUtilities;
import data.EvaluationContext;
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
	 * Context on which the evaluation will be made, the recommender must use the same context
	 * to obtain coherent results.
	 */
	EvaluationContext<T, U> context;

	/**
	 * The tv recommender to evaluate.
	 */
	AbstractTVRecommender<T, U> recommender;

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
	public TVRecommenderEvaluator(EvaluationContext<T, U> context, AbstractTVRecommender<T, U> recommender, EvaluationMeasure[] measures) {
		this.context = context;
		this.measures = measures;
		this.recommender = recommender;
		this.evaluationResults = new HashMap<EvaluationMeasure, Double>();
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
	 * Method that output the results in a file.
	 * @param outputPath The output file path.
	 */
	public void outputResults(String outputPath, String title){
		String resultsAsString = evaluationResults.entrySet().stream().map(entry -> entry.getKey().getClass().getSimpleName() + ": " + entry.getValue()).collect(Collectors.joining("\n"));
		Path file = Paths.get(outputPath);
		String results = title + "\n" + resultsAsString + "\n";
		try {
			Files.write(file, Arrays.asList(results), StandardCharsets.UTF_8,
			    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		List<Integer> userIds = context.getTestSet().getAllUserIds();
		double meanAveragePrecision = 0.0d;
		long startTime = System.currentTimeMillis();
		long total = userIds.size();
		long current = 0;
		for (int userId : userIds) {
			current++;
			meanAveragePrecision += calculateAveragePrecisionForUser(userId, numberOfResults);
			printProgress(startTime, total, current, meanAveragePrecision / current);
		}
		return meanAveragePrecision /= userIds.size();
	}

	private double calculateAveragePrecisionForUser(int userId, int numberOfResults) {
		List<Integer> groundTruth = context.getGroundTruth().get(userId);
		double averagePrecision = 0.0d;
		List<Integer> recommendedTVShowIndexes = recommender.recommend(userId, numberOfResults, context.getTestPrograms()).stream().map(rec -> rec.getProgram().getProgramId()).collect(toList());
		averagePrecision = calculateAveragePrecision(numberOfResults, recommendedTVShowIndexes, groundTruth);
		return averagePrecision;
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
				averagePrecision += truePositiveRecommendedTVShow / k;
			}
		}
		averagePrecision /= actuallySeenTVShowIndexes.size();
		return averagePrecision;
	}

	public static void main(String[] args) {
		LocalDateTime trainingStartTime = RecsysTVDataSet.START_TIME;
		LocalDateTime trainingEndTime = RecsysTVDataSet.START_TIME.plusDays(2);
		LocalDateTime testStartTime = RecsysTVDataSet.START_TIME.plusDays(2);
		LocalDateTime testEndTime = RecsysTVDataSet.START_TIME.plusDays(3);
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader
				.loadDataSet(minDuration);
		RecsysEPG epg = data._1;
		RecsysTVDataSet events = data._2;
		epg.cache(); events.cache();
		EvaluationContext<RecsysTVProgram, RecsysTVEvent> context = new EvaluationContext<RecsysTVProgram, RecsysTVEvent>(epg, events, trainingStartTime, trainingEndTime, testStartTime, testEndTime);
		for(int k = 1; k < 100; k+=5){
			SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent> recommender = new SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent>(context, new RecsysBooleanFeatureExtractor(epg), k, 50, sc);
			recommender.train();
			EvaluationMeasure[] measures = { EvaluationMeasure.MEAN_AVERAGE_PRECISION_AT_10 };
			TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent> evaluator = new TVRecommenderEvaluator<RecsysTVProgram, RecsysTVEvent>(context, recommender, measures);
			evaluator.evaluate();
			//evaluator.outputResults("resultx.txt", "Space Alignment Recommender (rank@" + k + ")");
		}
	}
	
	private static void printProgress(long startTime, long total, long current, double currentMetricScore) {
	    long eta = current == 0 ? 0 : 
	        (total - current) * (System.currentTimeMillis() - startTime) / current;

	    String etaHms = current == 0 ? "N/A" : 
	            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
	                    TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
	                    TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

	    StringBuilder string = new StringBuilder(140);   
	    int percent = (int) (current * 100 / total);
	    string
	        .append('\r')
	        .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
	        .append(String.format(" %d%% [", percent))
	        .append(String.join("", Collections.nCopies(percent, "=")))
	        .append('>')
	        .append(String.join("", Collections.nCopies(100 - percent, " ")))
	        .append(']')
	        .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
	        .append(String.format(" %d/%d, ETA: %s", current, total, etaHms))
	    	.append("|")
	    	.append(currentMetricScore);
	    System.out.print(string);
	}
}
