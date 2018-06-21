package evaluator.visualisation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

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
	
	public static void plotEvaluationResultsTimeSeries(YIntervalSeriesCollection errorTimeSeriesCollection, String outputPath){
	      JFreeChartUtilities.createAndSaveTimeErrorDeviationChart("", "Day", "Score", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, errorTimeSeriesCollection);
	}
	
	public static <U extends User> void plotUserResultSortedByTime(TimeSeriesCollection timeSeriesCollection, String outputPath){
	      JFreeChartUtilities.createAndSaveTimeSeriesChart("", "Date", "Score", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, timeSeriesCollection);
	}
	
	public static void plotSortedUsersScores(XYSeriesCollection xySeriesCollection, String outputPath){
	      JFreeChartUtilities.createAndSaveXYChart("", "Users", "Score", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, xySeriesCollection);
	}
	
	public static void plotTrainingTestNumberOfEventsSortedByUsersScore(XYSeriesCollection xySeriesCollection, String outputPath) {
	      JFreeChartUtilities.createAndSaveXYChart("", "Users", "Number of Events", DEFAULT_WIDTH, DEFAULT_HEIGHT, outputPath, xySeriesCollection);
	}
	
	public static YIntervalSeries convertEvaluationResultsToErrorSeries(Set<? extends EvaluationResult<?, ?>> evaluationResults, String metricName, String seriesName){
		YIntervalSeries metricErrorSeries = new YIntervalSeries(seriesName);
		for(EvaluationResult<?, ?> evaluationResult : evaluationResults) {
			long trainingEndTimeDay = evaluationResult.evaluationInfo().getTrainingEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			MetricResults<?> metricResult = evaluationResult.metricResult(metricName);
			double mean = metricResult.mean();
			double std = metricResult.std();
			metricErrorSeries.add(trainingEndTimeDay, mean, Math.max(mean - std, 0), mean + std);
		}
		return metricErrorSeries;
	}
	
	public static <U extends User> TimeSeries convertEvaluationResultsToTimeSeriesForUser(Set<? extends EvaluationResult<U, ?>> evaluationResults, U user, String metricName, String seriesName){
		TimeSeries metricTimeSeries = new TimeSeries(seriesName);
		for(EvaluationResult<U, ?> evaluationResult : evaluationResults) {
			LocalDateTime trainingEndTime = evaluationResult.evaluationInfo().getTrainingEndTime();
			Day trainingEndTimeDay = new Day(trainingEndTime.getDayOfMonth(), trainingEndTime.getMonthValue(), trainingEndTime.getYear());
			metricTimeSeries.add(trainingEndTimeDay, evaluationResult.metricResult(metricName).userScore(user));
		}
		return metricTimeSeries;
	}
	
	public static XYSeries convertEvaluationResultToSortedUsersScoresXYSeries(EvaluationResult<?, ?> evaluationResult, String metricName, String seriesName){
		MetricResults<?> metricResult = evaluationResult.metricResult(metricName);
		XYSeries metricSeries = new XYSeries(seriesName);
		StreamUtilities.zipWithIndex(metricResult.userScores().values().stream().sorted())
				.forEach(scoreWithIndex -> metricSeries.add((int)scoreWithIndex._2(), scoreWithIndex._1()));
		return metricSeries;
	}
}
