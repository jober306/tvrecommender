package util.visualisation;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class VisualisationUtilities {
	
	public static void createAndSaveXYChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, XYSeries... allSeries){
		final XYSeriesCollection seriesCollection = new XYSeriesCollection();
		for(XYSeries series : allSeries){
			seriesCollection.addSeries(series);
		}
	    JFreeChart xylineChart = ChartFactory.createXYLineChart(title, xTitle, yTitle, seriesCollection);
	    saveChartAsJPEG(outputPath, xylineChart, width, height);
	}
	
	public static void createAndSaveTimeSeriesChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, TimeSeries... allSeries){
		final TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
		for(TimeSeries series : allSeries){
			seriesCollection.addSeries(series);
		}
	    JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(title, xTitle, yTitle, seriesCollection);
	    saveChartAsJPEG(outputPath, timeSeriesChart, width, height);
	}
	
	public static void saveChartAsJPEG(String outputPath, JFreeChart chart, int width, int height){
	    File plotFile = new File( outputPath);
	    try {
	    	ChartUtils.saveChartAsJPEG(plotFile, chart, width, height );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
