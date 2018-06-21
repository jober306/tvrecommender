package util.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 * Class that offers utilities methods related to JFreeChart.
 * @author Jonathan Bergeron
 *
 */
public class JFreeChartUtilities {
	
	public static void createAndSaveBarChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, CategoryDataset categoryDataset) {
		JFreeChart xyBarChart = ChartFactory.createBarChart(title, xTitle, yTitle, categoryDataset);
	    saveChartAsJPEG(outputPath, xyBarChart, width, height);
	}
	
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
	public static void createAndSaveXYChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, XYSeriesCollection seriesCollection){
	    JFreeChart xylineChart = ChartFactory.createXYLineChart(title, xTitle, yTitle, seriesCollection);
	    saveChartAsJPEG(outputPath, xylineChart, width, height);
	}
	
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
	public static void createAndSaveXYErrorChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, YIntervalSeriesCollection seriesCollection){
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
	public static void createAndSaveTimeSeriesChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, TimeSeriesCollection seriesCollection){
	    JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(title, xTitle, yTitle, seriesCollection);
	    saveChartAsJPEG(outputPath, timeSeriesChart, width, height);
	}
	
	/**
	 * Method that creates a error deviation chart and saves it as jpeg.
	 * It assumes that the x data in the series represents a day.
	 * @param title The title of the chart.
	 * @param xTitle The x axis title.
	 * @param yTitle The y axis title.
	 * @param width The width of the jpeg chart.
	 * @param height The height of the jpeg chart.
	 * @param outputPath The output path of the jpeg chart.
	 * @param allSeries An array containing all the y interval series of points to plot in the chart.
	 */
	public static void createAndSaveTimeErrorDeviationChart(String title, String xTitle, String yTitle, int width, int height, String outputPath, YIntervalSeriesCollection seriesCollection) {
		boolean legends = true;
		boolean tooltips = true;
		boolean urls = false;
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		JFreeChart chart = ChartFactory.createXYLineChart(title, xTitle, yTitle, seriesCollection, orientation, legends, tooltips, urls);
		chart.setBackgroundPaint(Color.white);

		// customise the plot
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		// replace the number axis with a date axis
		DateAxis dateAxis = new DateAxis("Date");
		dateAxis.setLowerMargin(0.0d);
		dateAxis.setUpperMargin(0.0d);
		plot.setDomainAxis(dateAxis);

		// customise the range axis...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRangeIncludesZero(false);

		// customise the renderer...
		DeviationRenderer renderer = new DeviationRenderer(true, false);
		renderer.setAlpha(0.1f);
		List<Color> colors = Arrays.asList(Color.red, Color.blue, Color.green, Color.yellow);
		for(int i = 0; i < 4; i++) {
			renderer.setSeriesStroke(i, new BasicStroke(3.0f));
			renderer.setSeriesFillPaint(i, colors.get(i));
			renderer.setSeriesFillPaint(i, colors.get(i));
		}
		plot.setRenderer(renderer);	
		
		saveChartAsJPEG(outputPath, chart, width, height);
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
