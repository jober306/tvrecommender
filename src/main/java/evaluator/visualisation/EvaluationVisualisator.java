package evaluator.visualisation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import model.data.User;
import util.collections.StreamUtilities;
import util.jfreechart.JFreeChartUtilities;

public class EvaluationVisualisator {
	
	private static final int DEFAULT_WIDTH = 560;
	private static final int DEFAULT_HEIGHT = 370;
	
	public static void plotEvaluationResultsTimeSeries(Set<? extends EvaluationResult<?, ?>> results, String metricToShow, String recommenderName, String outputPath){
	      Map<String, YIntervalSeries> metricSeries = convertEvaluationResultsToErrorSeries(results, recommenderName);
	      YIntervalSeriesCollection seriesCollections = new YIntervalSeriesCollection();
	      if(metricSeries.containsKey(metricToShow)) {
	    	  seriesCollections.addSeries(metricSeries.get(metricToShow));
	      }
	      JFreeChartUtilities.createAndSaveTimeErrorDeviationChart("", "Day", "Score", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, seriesCollections);
	}
	
	public static <U extends User> void plotUserResultSortedByTime(Set<? extends EvaluationResult<U, ?>> results, U user, String outputPath, Set<String> metricsToShow){
	      Map<String, TimeSeries> metricSeries = convertEvaluationResultsToTimeSeriesForUser(results, user);
	      TimeSeriesCollection seriesCollections = new TimeSeriesCollection();
	      for(String metricToShow : metricsToShow){
	    	  if(metricSeries.containsKey(metricToShow)) {
	    		  seriesCollections.addSeries(metricSeries.get(metricToShow));
	    	  }
	      }
	      JFreeChartUtilities.createAndSaveTimeSeriesChart("", "Date", "Score", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, seriesCollections);
	}
	
	public static void plotSortedUsersScores(EvaluationResult<?, ?> result, String outputPath, String metricToShow){
	      Map<String, XYSeries> metricSeries = convertEvaluationResultToSortedUsersScoresXYSeries(result);
	      XYSeriesCollection seriesCollections = new XYSeriesCollection();
	      if(metricSeries.containsKey(metricToShow)) {
	    	  seriesCollections.addSeries(metricSeries.get(metricToShow));
	      }
	      JFreeChartUtilities.createAndSaveXYChart("", "Day", "Score", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, seriesCollections);
	}
	
	public static Map<String, YIntervalSeries> convertEvaluationResultsToErrorSeries(Set<? extends EvaluationResult<?, ?>> evaluationResults, String seriesName){
		Map<String, YIntervalSeries> metricIntervalSeries = getAllEvaluatedMetricsFrom(evaluationResults).stream()
				.collect(Collectors.toMap(Function.identity(), metricName -> new YIntervalSeries(seriesName)));
		for(EvaluationResult<?, ?> evaluationResult : evaluationResults) {
			long trainingEndTimeDay = evaluationResult.evaluationInfo().getTrainingEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			for(MetricResults<?> metricResults : evaluationResult.metricsResults().values()) {
				double mean = metricResults.mean();
				double std = metricResults.std();
				metricIntervalSeries.get(metricResults.metricName()).add(trainingEndTimeDay, mean, Math.max(mean - std, 0), mean + std);
			}
		}
		return metricIntervalSeries;
	}
	
	public static <U extends User> Map<String, TimeSeries> convertEvaluationResultsToTimeSeriesForUser(Set<? extends EvaluationResult<U, ?>> evaluationResults, U user){
		Map<String, TimeSeries> metricTimeSeries = getAllEvaluatedMetricsFrom(evaluationResults).stream()
				.collect(Collectors.toMap(Function.identity(), metricName -> new TimeSeries(metricName)));
		for(EvaluationResult<U, ?> evaluationResult : evaluationResults) {
			LocalDateTime trainingEndTime = evaluationResult.evaluationInfo().getTrainingEndTime();
			Day trainingEndTimeDay = new Day(trainingEndTime.getDayOfMonth(), trainingEndTime.getMonthValue(), trainingEndTime.getYear());
			for(MetricResults<U> metricResults : evaluationResult.metricsResults().values()) {
				double userResult = metricResults.userScore(user);
				metricTimeSeries.get(metricResults.metricName()).add(trainingEndTimeDay, userResult);
			}
		}
		return metricTimeSeries;
	}
	
	public static Map<String, XYSeries> convertEvaluationResultToSortedUsersScoresXYSeries(EvaluationResult<?, ?> evaluationResult){
		Map<String, XYSeries> metricsXYSeries = evaluationResult.metricsResults().values().stream()
				.map(metricResult -> metricResult.metricName())
				.collect(Collectors.toMap(Function.identity(), metricName -> new XYSeries(metricName)));
		evaluationResult.metricsResults().values().stream().forEach(metricResult -> {
			final XYSeries metricXYSeries = metricsXYSeries.get(metricResult.metricName());
			StreamUtilities.zipWithIndex(metricResult.userScores().values().stream().sorted())
				.forEach(scoreWithIndex -> metricXYSeries.add((int)scoreWithIndex._2(), scoreWithIndex._1()));
		});
		return metricsXYSeries;
	}
	
	private static Set<String> getAllEvaluatedMetricsFrom(Set<? extends EvaluationResult<?, ?>> evaluationResults){
		return evaluationResults.stream()
				.flatMap(evaluationResult -> evaluationResult.metricsResults().values().stream())
				.map(MetricResults::metricName)
				.distinct()
				.collect(Collectors.toSet());
	}
}
