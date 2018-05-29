package evaluator.visualisation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jfree.data.xy.YIntervalSeriesCollection;

import data.recsys.RecsysTVProgram;
import evaluator.result.EvaluationResult;
import jersey.repackaged.com.google.common.collect.Sets;
import model.data.TVProgram;
import model.data.User;
import scala.Tuple2;
import util.jfreechart.JFreeChartUtilities;
public class EvaluationVisualisatorDriver {
	
	final static String RESULTS_DIR_PATH = "src/main/resources/results/";

	
	public static void main(String[] args) {
		Set<String> recommenderNames = Sets.newHashSet("topchannel", "topchannelperuser", "topchannelperuserperslot", "spacealignment");
		Stream<String> allMetrics = Stream.of("Diversity", "Novelty", "Recall@10", "AveragePrecision@10");
		String resultTypeName = "window";
		allMetrics.forEach(metricToShow -> plotRecommenderAgainstRecommenderResult(recommenderNames, resultTypeName, metricToShow));
		//recommenderNames.stream().forEach(recommenderName -> plotSortedUsersResult(recommenderName, resultTypeName, metricToShow));
	}
	
	private static void plotRecommenderAgainstRecommenderResult(Set<String> recommenderNames, String resultType, String metricToShow) {
		String outputPath =  RESULTS_DIR_PATH + "recommenders_" + resultType + "_" + metricToShow + ".jpeg";
		
		//Gather all evaluation results
		YIntervalSeriesCollection seriesCollections = new YIntervalSeriesCollection();
		recommenderNames.stream()
			.map(recommenderName -> new Tuple2<>(recommenderName, RESULTS_DIR_PATH + recommenderName + "/" + resultType))
			.map(recommenderNameAndPath -> new Tuple2<>(recommenderNameAndPath._1(), EvaluationVisualisatorDriver.deserializeRecommenderResults(recommenderNameAndPath._2())))
			.map(recommenderNameAndResults -> EvaluationVisualisator.convertEvaluationResultsToErrorSeries(recommenderNameAndResults._2(), recommenderNameAndResults._1()))
			.map(seriesMap -> seriesMap.get(metricToShow))
			.forEach(seriesCollections::addSeries);

		//Plot and save the evaluation results.
		JFreeChartUtilities.createAndSaveTimeErrorDeviationChart("", "Day", "Score", 560, 370, outputPath, seriesCollections);
	}
	
	private static void plotSortedUsersResult(String recommenderName, String resultType, String metricToShow) {
		String recommenderResultsDirectory = RESULTS_DIR_PATH + recommenderName + "/" + resultType;
		deserializeRecommenderResults(recommenderResultsDirectory).stream()
			.forEach(result -> EvaluationVisualisator.plotSortedUsersScores(result, recommenderResultsDirectory + "/" + result.generateFileName() + ".jpeg", metricToShow));
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
