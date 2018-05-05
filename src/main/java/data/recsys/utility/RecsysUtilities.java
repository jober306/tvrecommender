package data.recsys.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;

import com.google.common.collect.ImmutableMap;

import data.recsys.RecsysTVEvent;

/**
 * Class that provides some utilities methods related to the recsys classes.
 * @author Jonathan Bergeron
 *
 */
public class RecsysUtilities {

	/**
	 * The date the data set started recording tv audience behavior (This is not
	 * the real one). Monday, april 1995 at midnigh.
	 */
	public static final LocalDateTime START_TIME = LocalDateTime.of(1995, Month.APRIL, 10, 0, 0);
	
	final static String GENRE_SUBGENRE_MAPPING_PATH = "/tv-audience-dataset/genreSubgenreMapping.txt";
	
	final static Map<Byte, String> genreToNameMap;
	final static Map<Byte, Map<Byte,String>> subgenreToNameMap;
	
	static{
		genreToNameMap = new HashMap<Byte,String>();
		subgenreToNameMap = new HashMap<Byte, Map<Byte,String>>();
		try {
			InputStream genreSubgenreStream = RecsysUtilities.class.getResourceAsStream(GENRE_SUBGENRE_MAPPING_PATH);
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
	
	static Map<Short, Integer> channelIDMap;
	static Map<Short, Integer> slotIDMap;
	static Map<Byte, Integer> genreIDMap;
	static Map<Byte, Integer> subgenreIDMap;
	
	static{
		int mappedID = 0;
		Map<Short, Integer> tempChannelIDMap = new HashMap<Short, Integer>();
		for (short channelID = 1; channelID <= 217; channelID++) {
			tempChannelIDMap.put(channelID, mappedID);
			mappedID++;
		}
		channelIDMap = ImmutableMap.copyOf(tempChannelIDMap);

		Map<Short, Integer> tempSlotIDMap = new HashMap<Short, Integer>();
		for(short slotID = 1; slotID <= 168; slotID++){
			tempSlotIDMap.put(slotID, mappedID);
			mappedID++;
		}
		slotIDMap = ImmutableMap.copyOf(tempSlotIDMap);

		Map<Byte, Integer> tempGenreIDMap = new HashMap<Byte, Integer>();
		for (byte genreID = 1; genreID <= 8; genreID++) {
			tempGenreIDMap.put(genreID, mappedID);
			mappedID++;
		}
		genreIDMap = ImmutableMap.copyOf(tempGenreIDMap);

		Map<Byte, Integer> tempSubGenreIDMap = new HashMap<Byte, Integer>();
		for (byte subgenreID = 1; subgenreID <= 114; subgenreID++) {
			tempSubGenreIDMap.put(subgenreID, mappedID);
			mappedID++;
		}
		subgenreIDMap = ImmutableMap.copyOf(tempSubGenreIDMap);
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
	 * Getter method that returns the channelIDMap. The map is read-only.
	 * 
	 * @return The read-only channelIDMap
	 */
	public static Map<Short, Integer> getChannelIDMap() {
		return channelIDMap;
	}
	
	/**
	 * Method that returns the slot id map.
	 * @return The slot id map.
	 */
	public static Map<Short, Integer> getSlotIDMap(){
		return slotIDMap;
	}
	
	/**
	 * Getter method that returns the genreIDMap. The map is read-only.
	 * 
	 * @return The read-only genreIDMap
	 */
	public static Map<Byte, Integer> getGenreIDMap() {
		return genreIDMap;
	}

	/**
	 * Getter method that returns the subgenreIDMap. The map is read-only.
	 * 
	 * @return The read-only subgenreIDMap
	 */
	public static Map<Byte, Integer> getSubgenreIDMap() {
		return subgenreIDMap;
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
	
	/**
	 * Method that calculate the start time given a week and a slot.
	 * Slot in the recsys tv data set are considered to be hours.
	 * @param week The week.
	 * @param slot The slot.
	 * @return The time corresponding to this week and slot.
	 */
	public static LocalDateTime getStartTimeFromWeekAndSlot(short week, short slot){
		return START_TIME.plusWeeks(week-1).plusHours(slot-1);
	}
	
	/**
	 * Method that calculate the start time given a week and a slot.
	 * Slot in the recsys tv data set are considered to be hours.
	 * @param week The week.
	 * @param slot The slot.
	 * @return The time corresponding to this week and slot.
	 */
	public static LocalDateTime getEndTimeFromWeekAndSlot(short week, short slot){
		return START_TIME.plusWeeks(week-1).plusHours(slot);
	}
	
	/**
	 * Methods that calculate the distance between two slots. The distance function is given in this paper:
	 * http://www.contentwise.tv/files/Time_based_TV_programs_prediction_Paper.pdf
	 * @param slot1 The first slot.
	 * @param slot2 The second slot.
	 * @return The distance between the two slots.
	 */
	public static int slotDistance(int slot1, int slot2){
		int dayDist = dayDistance(slot1, slot2);
		int minDist = minDistance(slot1, slot2);
		int indicFunction = bothWeekendOrBothWeekDay(slot1, slot2); 
		return (dayDist + (minDist / 60)) * indicFunction;
	}
	
	private static int dayDistance(int slot1, int slot2){
		int day1 = (slot1 -1) % 24;
		int day2 = (slot2 -1) % 24;
		return Math.abs(day1 - day2);
	}
	
	private static int minDistance(int slot1, int slot2){
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
	
	private static int bothWeekendOrBothWeekDay(int slot1, int slot2){
		if((isAWeekDay(slot1) && isAWeekDay(slot2)) || (isAWeekendDay(slot1) && isAWeekendDay(slot2))){
			return 1;
		}else{
			return 0;
		}
	}
	
	private static boolean isAWeekDay(int slot){
		int day = (slot -1) % 24;
		return day <= 4;
	}
	
	private static boolean isAWeekendDay(int slot){
		int day = (slot -1) % 24;
		return day >= 5;
	}
}
