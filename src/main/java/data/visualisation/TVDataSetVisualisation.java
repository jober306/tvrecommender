package data.visualisation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import data.TVDataSet;
import data.recsys.RecsysTVDataSet;
import data.recsys.loader.RecsysTVDataSetLoader;
import model.data.TVEvent;
import util.function.SerializableFunction;
import util.jfreechart.JFreeChartUtilities;

public class TVDataSetVisualisation implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static <E extends TVEvent<?, ?>> void createAndSaveSortedProgramIdCountChart(TVDataSet<?, ?, E> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(TVEvent::getProgramID, dataset, "");
		String plotTitle = "";
		String yAxisTitle = "Nombre de vues";
		int width = 560;
		int height = 370;
		String outputPath = outputDir + "programCount.jpeg";
	    JFreeChartUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static <E extends TVEvent<?, ?>> void createAndSaveSortedChannelCountChart(TVDataSet<?, ?, E> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(TVEvent::getChannelId, dataset, "");
		String plotTitle = "";
		String yAxisTitle = "Number of Views";
		int width = 560;
		int height = 370;
		String outputPath = outputDir + "channelCount.jpeg";
	    JFreeChartUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static <E extends TVEvent<?, ?>> void createAndSaveSortedUserCountChart(TVDataSet<?, ?, E> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(TVEvent::getUserID, dataset, "");
		String plotTitle = "";
		String yAxisTitle = "Number of Views";
		int width = 560;
		int height = 370;
		String outputPath = outputDir + "userCount.jpeg";
	    JFreeChartUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static <E extends TVEvent<?, ?>> XYSeries getSortedCountSeriesOf(SerializableFunction<? super E, ?>  tvEventKeyMapper, TVDataSet<?, ?, E> dataset, String seriesName){
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
		RecsysTVDataSet dataset = loader.loadDataSet(5)._2();
		TVDataSetVisualisation.createAndSaveSortedChannelCountChart(dataset, "src/main/resources/results/data/");
		TVDataSetVisualisation.createAndSaveSortedUserCountChart(dataset, "src/main/resources/results/data/");
	}
}
