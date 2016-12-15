package data.recsys.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;

import data.model.TVEvent;
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
	 * Method that filters out all the events that occurred before min week and after max week.
	 * @param minWeek The minimum week number.
	 * @param maxWeek The maximum week number.
	 * @return The filtered tv events.
	 */
	public static JavaRDD<RecsysTVEvent> filterByIntervalOfWeek(JavaRDD<RecsysTVEvent> events, int minWeek, int maxWeek){
		return events.filter(tvEvent -> tvEvent.getWeek() >= minWeek && tvEvent.getWeek() <= maxWeek);
	}
	
	/**
	 * Method that filters out all the events that occurred before min slot and after max slot.
	 * @param minSlot The minimum week number.
	 * @param maxSlot The maximum week number.
	 * @return The filtered tv events.
	 */
	public static JavaRDD<RecsysTVEvent> filterByIntervalOfSlot(JavaRDD<RecsysTVEvent> events, int minSlot, int maxSlot){
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
	public static JavaRDD<RecsysTVEvent> filterByIntervalOfDay(JavaRDD<RecsysTVEvent> events, int minDay, int maxDay){
		return filterByIntervalOfSlot(events, (minDay-1)*24 + 1, (maxDay)*24);
	}
	
	/**
	 * Method that check if both map are not empty.
	 * @return True if both map are not empty, false otherwise.
	 */
	public static boolean isGenreSubgenreMapNotEmpty(){
		return !genreToNameMap.isEmpty() && !subgenreToNameMap.isEmpty();
	}
	
	public static LocalDateTime getStartTimeFromWeekAndSlot(short week, short slot){
		return RecsysTVDataSet.START_TIME.plusWeeks(week-1).plusHours(slot-1);
	}
	
	public static LocalDateTime getEndTimeFromWeekAndSlot(short week, short slot){
		return RecsysTVDataSet.START_TIME.plusWeeks(week-1).plusHours(slot);
	}
	
	public int slotDistance(int slot1, int slot2){
		int dayDist = dayDistance(slot1, slot2);
		int minDist = minDistance(slot1, slot2);
		int indicFunction = bothWeekendOrBothWeekDay(slot1, slot2); 
		return (dayDist + (minDist / 60)) * indicFunction;
	}
	
	public int dayDistance(int slot1, int slot2){
		int day1 = (slot1 -1) % 24;
		int day2 = (slot2 -1) % 24;
		return Math.abs(day1 - day2);
	}
	
	public int minDistance(int slot1, int slot2){
		int slot1Day = (slot1 -1) % 24;
		int slot2Day = (slot2 -1) % 24;
		if(Math.abs(slot1Day-slot2Day) > 12){
			int loweredMax = Math.max(slot1Day,slot2Day) -12;
			int min = Math.min(slot1Day, slot2Day);
			return Math.abs(loweredMax - min) * 60; 
		}else{
			return Math.abs(slot1Day - slot2Day) * 60;
		}
	}
	
	//TODO: Assuming the week start on monday
	public int bothWeekendOrBothWeekDay(int slot1, int slot2){
		if((isAWeekDay(slot1) && isAWeekDay(slot2)) || (isAWeekendDay(slot1) && isAWeekendDay(slot2))){
			return 1;
		}else{
			return 0;
		}
	}
	
	public boolean isAWeekDay(int slot){
		int day = (slot -1) % 24;
		return day <= 4;
	}
	
	public boolean isAWeekendDay(int slot){
		int day = (slot -1) % 24;
		return day >= 5;
	}
}
