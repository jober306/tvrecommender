package data.visualisation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;
import data.recsys.RecsysTVDataSet;
import data.recsys.loader.RecsysTVDataSetLoader;
import util.visualisation.VisualisationUtilities;

public class TVDataSetVisualisation {
	
	public static void createAndSaveSortedProgramCountChart(TVDataSet<? extends TVProgram, ? extends TVEvent> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(TVEvent::getProgramId, dataset, "Program Count");
		String plotTitle = "Sorted Program Watches Count";
		String yAxisTitle = "Number of Views";
		int width = 1080;
		int height = 720;
		String outputPath = outputDir + "programCount.jpeg";
	    VisualisationUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static void createAndSaveSortedChannelCountChart(TVDataSet<? extends TVProgram, ? extends TVEvent> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(TVEvent::getChannelId, dataset, "Program Count");
		String plotTitle = "Sorted Channel Watches Count";
		String yAxisTitle = "Number of Views";
		int width = 1080;
		int height = 720;
		String outputPath = outputDir + "channelCount.jpeg";
	    VisualisationUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static void plotUserCount(TVDataSet<? extends TVProgram, ? extends TVEvent> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(TVEvent::getUserID, dataset, "Program Count");
		String plotTitle = "Sorted User Watches Count";
		String yAxisTitle = "Number of Views";
		int width = 1080;
		int height = 720;
		String outputPath = outputDir + "userCount.jpeg";
	    VisualisationUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static <U extends TVEvent> XYSeries getSortedCountSeriesOf(Function<U, ?> tvEventKeyMapper, TVDataSet<?, U> dataset, String seriesName){
		Map<?, Long> programCount = dataset.getEventsData()
	  	     .map(tvEventKeyMapper::apply)
	  	     .countByValue();
		List<Long> sortedProgramCount = programCount.entrySet().stream()
	  	  	 .map(Entry::getValue)
	  		 .sorted()
	  		 .collect(Collectors.toList());
		XYSeries series = new XYSeries(seriesName);
	  	IntStream.range(0, sortedProgramCount.size()).boxed()
	  		.map(index -> new XYDataItem((double) index, (double) sortedProgramCount.get(index)))
	  	    .forEach(series::add);
	  	return series;
	}
	
	public static void main(String[] args){
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataset = loader.loadDataSet()._2();
		TVDataSetVisualisation.plotUserCount(dataset, "src/main/resources/results/data/");
	}
}
