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

/**
 * Class that offers utilities methods related to JFreeChart.
 * @author Jonathan Bergeron
 *
 */
public class VisualisationUtilities {
	
	/**
	 * Method that creates a XYChart and saves it as jpeg.
	 * @param title The title of the chart.
	 * @param xTitle The x axis title.
	 * @param yTitle The y axis title.
	 * @param width The width of the jpeg chart.
	 * @param height The height of the jpeg chart.
	 * @param outputPath The output path of the jpeg chart.
	 * @param allSeries An array containing all the xy series of points to plot in the chart.
	 */
	public static void createAndSaveXYChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, XYSeries... allSeries){
		final XYSeriesCollection seriesCollection = new XYSeriesCollection();
		for(XYSeries series : allSeries){
			seriesCollection.addSeries(series);
		}
	    JFreeChart xylineChart = ChartFactory.createXYLineChart(title, xTitle, yTitle, seriesCollection);
	    saveChartAsJPEG(outputPath, xylineChart, width, height);
	}
	
	/**
	 * Method that creates a TimeSeriesChart and saves it as jpeg.
	 * @param title The title of the chart.
	 * @param xTitle The x axis title.
	 * @param yTitle The y axis title.
	 * @param width The width of the jpeg chart.
	 * @param height The height of the jpeg chart.
	 * @param outputPath The output path of the jpeg chart.
	 * @param allSeries An array containing all the time series of points to plot in the chart.
	 */
	public static void createAndSaveTimeSeriesChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, TimeSeries... allSeries){
		final TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
		for(TimeSeries series : allSeries){
			seriesCollection.addSeries(series);
		}
	    JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(title, xTitle, yTitle, seriesCollection);
	    saveChartAsJPEG(outputPath, timeSeriesChart, width, height);
	}
	
	/**
	 * Method that takes a chart and saves it as a jpgeg.
	 * @param outputPath The output path of the jpeg chart.
	 * @param chart The chart to save as jpeg.
	 * @param width The width of the jpeg chart.
	 * @param height The height of the jpeg chart.
	 */
	public static void saveChartAsJPEG(String outputPath, JFreeChart chart, int width, int height){
	    File plotFile = new File( outputPath);
	    try {
	    	ChartUtils.saveChartAsJPEG(plotFile, chart, width, height );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
