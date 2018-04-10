package evaluator.visualisation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import util.collections.ListUtilities;

public class EvaluationVisualisator {
	
	public static void plotTimeSeries(Set<EvaluationResult> results){
	      Map<String, TimeSeries> metricTimeSeries = new HashMap<String, TimeSeries>();
	      List<String> allSharedMetrics = results.stream()
	    		  .map(EvaluationResult::metricsResults)
	    		  .map(resultList -> resultList.stream().map(MetricResults::metricName).collect(Collectors.toList()))
	    		  .reduce(ListUtilities::intersection)
	    		  .orElse(Collections.emptyList());
	      for(String metric : allSharedMetrics){
	    	  metricTimeSeries.put(metric, new TimeSeries(metric));
	      }
	      for(EvaluationResult result : results){
	    	  LocalDateTime trainingEndTime = result.evaluationInfo().getTrainingEndTime();
	    	  Day evaluationTrainingEndDay = new Day(trainingEndTime.getDayOfMonth(), trainingEndTime.getMonthValue(), trainingEndTime.getYear());
	    	  for(MetricResults metricResult : result.metricsResults()){
	    		  double score = metricResult.mean();
	    		  metricTimeSeries.get(metricResult.metricName()).add(evaluationTrainingEndDay, score);
	    	  }
	      }
	      TimeSeriesCollection series = new TimeSeriesCollection();
	      for(TimeSeries timeSeries : metricTimeSeries.values()){
	    	  series.addSeries(timeSeries);
	      }
	      final XYDataset dataset=( XYDataset ) series;
	      JFreeChart timechart = ChartFactory.createTimeSeriesChart(
	         "Top Channel Recommender One Month Training", 
	         "", 
	         "Score", 
	         dataset,
	         true, 
	         false, 
	         false);
	      int width = 560;   /* Width of the image */
	      int height = 370;  /* Height of the image */ 
	      File timeChart = new File( "TimeChart.jpeg" );
	      try {
			ChartUtils.saveChartAsJPEG(timeChart, timechart, width, height );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
