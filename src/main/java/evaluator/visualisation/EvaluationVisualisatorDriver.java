package evaluator.visualisation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaSparkContext;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeriesCollection;

import data.TVDataSet;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import evaluator.result.EvaluationInfo;
import evaluator.result.EvaluationResult;
import jersey.repackaged.com.google.common.collect.Sets;
import model.data.TVProgram;
import model.data.User;
import scala.Tuple2;
import scala.Tuple3;
import util.collections.StreamUtilities;
import util.jfreechart.JFreeChartUtilities;
import util.spark.SparkUtilities;

public class EvaluationVisualisatorDriver {
	
	final static String RESULTS_DIR_PATH = "src/main/resources/results/";

	
	public static void main(String[] args) {
		Set<String> recommenderNames = Sets.newHashSet("topchannel", "topchannelperuser", "topchannelperuserperslot", "spacealignment");
		Stream<String> allMetrics = Stream.of("Diversity", "Novelty", "Recall@10", "AveragePrecision@10");
		String resultTypeName = "window";
		//allMetrics.forEach(metricToShow -> plotRecommenderAgainstRecommenderResult(recommenderNames, resultTypeName, metricToShow));
		//plotSortedUsersResult("topchannelperuser", resultTypeName, "AveragePrecision@10");
		plotSortedUsersResultAndTrainingTestNumberOfEvents("topchannelperuser", resultTypeName, "AveragePrecision@10");
	}
	
	private static void plotRecommenderAgainstRecommenderResult(Set<String> recommenderNames, String resultType, String metricToShow) {
		String outputPath =  RESULTS_DIR_PATH + "recommenders_" + resultType + "_" + metricToShow + ".jpeg";
		
		//Gather all evaluation results
		YIntervalSeriesCollection seriesCollections = new YIntervalSeriesCollection();
		recommenderNames.stream()
			.map(recommenderName -> new Tuple2<>(recommenderName, RESULTS_DIR_PATH + recommenderName + "/" + resultType + "/evaluations"))
			.map(recommenderNameAndPath -> new Tuple2<>(recommenderNameAndPath._1(), EvaluationVisualisatorDriver.deserializeRecommenderResults(recommenderNameAndPath._2())))
			.map(recommenderNameAndResults -> EvaluationVisualisator.convertEvaluationResultsToErrorSeries(recommenderNameAndResults._2(), metricToShow, recommenderNameAndResults._1()))
			.forEach(seriesCollections::addSeries);

		//Plot and save the evaluation results.
		JFreeChartUtilities.createAndSaveTimeErrorDeviationChart("", "Day", "Score", 560, 370, outputPath, seriesCollections);
	}
	
	private static void plotSortedUsersResult(String recommenderName, String resultType, String metricToShow) {
		String recommenderResultsDirectory = RESULTS_DIR_PATH + recommenderName + "/" + resultType + "/evaluations";
		deserializeRecommenderResults(recommenderResultsDirectory).stream()
			.map(result -> new Tuple2<>(EvaluationVisualisator.convertEvaluationResultToSortedUsersScoresXYSeries(result, metricToShow, metricToShow), RESULTS_DIR_PATH + recommenderName + "/" + resultType + "/charts/" + result.generateFileName() + "_scores_" + metricToShow + ".jpeg"))
			.map(seriesAndOutputPath -> new Tuple2<>(new XYSeriesCollection(seriesAndOutputPath._1()), seriesAndOutputPath._2()))
			.forEach(seriesCollectionAndOutputPath -> EvaluationVisualisator.plotSortedUsersScores(seriesCollectionAndOutputPath._1(), seriesCollectionAndOutputPath._2()));
	}
	
	private static void plotSortedUsersResultAndTrainingTestNumberOfEvents(String recommenderName, String resultType, String metricToShow) {
		
		//Loading the data.
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		int minDuration = 5;
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet(minDuration);
		//caching data set
		data._2().cache();
		
		String recommenderResultsDirectory = RESULTS_DIR_PATH + recommenderName + "/" + resultType + "/evaluations";
		deserializeRecommenderResults(recommenderResultsDirectory).stream()
			.forEach(evalResult -> {
				XYSeries numberOfTrainingEventsSeries = new XYSeries("Number of training events");
				//XYSeries numberOfTestEventsSeries = new XYSeries("Number of test events");
				EvaluationInfo info = evalResult.evaluationInfo();
				TVDataSet<User, ?, ?> trainingSet = data._2().filterByDateTime(info.getTrainingStartTime(), info.getTrainingEndTime());
				TVDataSet<User, ?, ?> testSet = data._2().filterByDateTime(info.getTestStartTime(), info.getTestEndTime());
				StreamUtilities.zipWithIndex(evalResult.metricResult(metricToShow).userScores().keySet().stream()
					.map(user -> convertUserToTrainTestNumberOfEventsAndScore(evalResult, user, metricToShow, trainingSet, testSet))
					.sorted(Comparator.comparing(Tuple3::_3)))
					.forEach(trainTestNumberOfEvents -> {
						long index = trainTestNumberOfEvents._2();
						long numberOfTrainingEvents = trainTestNumberOfEvents._1()._1();
						double numberOfTestEvents = trainTestNumberOfEvents._1()._2();
						numberOfTrainingEventsSeries.add(index, numberOfTrainingEvents);
						//numberOfTestEventsSeries.add(index, numberOfTestEvents);
					});
				XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
				xySeriesCollection.addSeries(numberOfTrainingEventsSeries);
				//xySeriesCollection.addSeries(numberOfTestEventsSeries);
				String outputPath = RESULTS_DIR_PATH + recommenderName + "/" + resultType + "/charts/" + evalResult.generateFileName() + "_training_" + metricToShow + ".jpeg";
				EvaluationVisualisator.plotSortedUsersScores(xySeriesCollection, outputPath);
			});
	}
	
	private static Tuple3<Long, Long, Double> convertUserToTrainTestNumberOfEventsAndScore(EvaluationResult<User, ?> result, User user, String metricName, TVDataSet<User, ?, ?> trainingSet, TVDataSet<User, ?, ?> testSet){
		return new Tuple3<>(trainingSet.userTVEventCounts().getOrDefault(user, 0L), testSet.userTVEventCounts().getOrDefault(user, 0L), result.metricResult(metricName).userScore(user));
	}
	
	private static Set<EvaluationResult<User, RecsysTVProgram>> deserializeRecommenderResults(String recommenderResultsDirectory){
		try {
			return Files.list(Paths.get(recommenderResultsDirectory))
				.filter(path -> path.toString().endsWith(".ser"))
				.map(EvaluationVisualisatorDriver::<User, RecsysTVProgram>deserializeEvaluationResult)
				.collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static <U extends User, P extends TVProgram> EvaluationResult<U, P> deserializeEvaluationResult(Path path) {
		EvaluationResult<U, P> result = null;
		System.out.println("Deserializing " + path.toString());
		try {
			ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path));
			result = (EvaluationResult<U, P>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
