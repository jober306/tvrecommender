package data.visualisation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import data.AbstractTVEvent;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;
import data.recsys.RecsysTVDataSet;
import data.recsys.loader.RecsysTVDataSetLoader;
import util.function.SerializableFunction;
import util.jfreechart.JFreeChartUtilities;

public class TVDataSetVisualisation implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static void createAndSaveSortedProgramIdCountChart(TVDataSet<? extends TVProgram, ? extends AbstractTVEvent<?>> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(AbstractTVEvent::getProgramID, dataset, "");
		String plotTitle = "";
		String yAxisTitle = "Nombre de vues";
		int width = 560;
		int height = 370;
		String outputPath = outputDir + "programCount.jpeg";
	    JFreeChartUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static void createAndSaveSortedChannelCountChart(TVDataSet<? extends TVProgram, ? extends AbstractTVEvent<?>> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(AbstractTVEvent::getChannelId, dataset, "");
		String plotTitle = "";
		String yAxisTitle = "Number of Views";
		int width = 560;
		int height = 370;
		String outputPath = outputDir + "channelCount.jpeg";
	    JFreeChartUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static void createAndSaveSortedUserCountChart(TVDataSet<? extends TVProgram, ? extends AbstractTVEvent<?>> dataset, String outputDir){
		XYSeries sortedProgramCount = getSortedCountSeriesOf(AbstractTVEvent::getUserID, dataset, "");
		String plotTitle = "";
		String yAxisTitle = "Number of Views";
		int width = 560;
		int height = 370;
		String outputPath = outputDir + "userCount.jpeg";
	    JFreeChartUtilities.createAndSaveXYChart(plotTitle, "", yAxisTitle, width, height, outputPath, sortedProgramCount);
	}
	
	public static <U extends AbstractTVEvent<?>> XYSeries getSortedCountSeriesOf(SerializableFunction<U, ?>  tvEventKeyMapper, TVDataSet<?, U> dataset, String seriesName){
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
