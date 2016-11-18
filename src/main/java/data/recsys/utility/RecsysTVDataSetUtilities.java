package data.recsys.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;

/**
 * Class that provides some utilities methods related to the recsys tv data set.
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSetUtilities {
	
	static final String GENRE_SUBGENRE_MAPPING_PATH = "/tv-audience-dataset/genreSubgenreMapping.txt";
	
	static Map<Byte, String> genreToNameMap;
	static Map<Byte, Map<Byte,String>> subgenreToNameMap;
	static{
		genreToNameMap = new HashMap<Byte,String>();
		subgenreToNameMap = new HashMap<Byte, Map<Byte,String>>();
		try {
			InputStream genreSubgenreStream = RecsysTVDataSetUtilities.class.getResourceAsStream(GENRE_SUBGENRE_MAPPING_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(genreSubgenreStream));
			String line = "";
			while((line = br.readLine())!= null){
				String[] lineInfos = line.split(" ");
				String genre = lineInfos[0];
				Byte genreID = Byte.parseByte(lineInfos[1]);
				String subgenre = lineInfos[2];
				Byte subgenreID = Byte.parseByte(lineInfos[3]);
				genreToNameMap.put(genreID, genre);
				if(!subgenreToNameMap.containsKey(genreID)){
					subgenreToNameMap.put(genreID, new HashMap<Byte,String>());
				}
				subgenreToNameMap.get(genreID).put(subgenreID, subgenre);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter method that maps a genre id to the genre it represents.
	 * @param genreID The genre id.
	 * @return The genre it represents.
	 */
	public static String getGenreName(byte genreID){
		return genreToNameMap.get(genreID);
	}
	
	/**
	 * Getter method that maps a subgenre id to the subgenre it represents.
	 * @param subgenreID The subgenre id.
	 * @return The subgenre it represents.
	 */
	public static String getSubgenreName(byte genreID, byte subgenreID){
		return subgenreToNameMap.get(genreID).get(subgenreID);
	}
	
	/**
	 * Method that check if both map are not empty.
	 * @return True if both map are not empty, false otherwise.
	 */
	public static boolean isGenreSubgenreMapNotEmpty(){
		return !genreToNameMap.isEmpty() && !subgenreToNameMap.isEmpty();
	}
	
	/**
	 * Method that returns tv events that have at least been viewed minTimeView
	 * time.
	 * 
	 * @param minTimeView
	 *            The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least
	 *         minTimeView time.
	 */
	public static JavaRDD<RecsysTVEvent> filterByMinTimeView(RecsysTVDataSet tvDataSet, int minTimeView) {
		JavaRDD<RecsysTVEvent> events = tvDataSet.getEventsData();
		return events.filter(tvEvent -> tvEvent.getDuration() >= minTimeView);
	}
	
	/**
	 * Method that filters out all the events that occurred before min week and after max week.
	 * @param minWeek The minimum week number.
	 * @param maxWeek The maximum week number.
	 * @return The filtered tv events.
	 */
	public static JavaRDD<RecsysTVEvent> filterByIntervalOfWeek(RecsysTVDataSet tvDataSet, int minWeek, int maxWeek){
		JavaRDD<RecsysTVEvent> events = tvDataSet.getEventsData();
		return events.filter(tvEvent -> tvEvent.getWeek() >= minWeek && tvEvent.getWeek() <= maxWeek);
	}
	
	/**
	 * Method that filters out all the events that occurred before min slot and after max slot.
	 * @param minSlot The minimum week number.
	 * @param maxSlot The maximum week number.
	 * @return The filtered tv events.
	 */
	public static JavaRDD<RecsysTVEvent> filterByIntervalOfSlot(RecsysTVDataSet tvDataSet, int minSlot, int maxSlot){
		JavaRDD<RecsysTVEvent> events = tvDataSet.getEventsData();
		return events.filter(tvEvent -> tvEvent.getSlot() >= minSlot && tvEvent.getSlot() <= maxSlot);
	}
	
	/**
	 * Method that filters out all the events that occurred before min day and after max day.
	 * The data set does not specify at what day the week start. Min day and Max day take values
	 * between 1 (the first day of the week) and 7 (the last day of the week).
	 * @param minDay The minimum day number.
	 * @param maxDay The maximum day number.
	 * @return The filtered tv events.
	 */
	public static JavaRDD<RecsysTVEvent> filterByIntervalOfDay(RecsysTVDataSet tvDataSet, int minDay, int maxDay){
		return filterByIntervalOfSlot(tvDataSet, (minDay-1)*24 + 1, (maxDay)*24);
	}

}
