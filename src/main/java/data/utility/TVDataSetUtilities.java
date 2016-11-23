package data.utility;

import org.apache.spark.api.java.JavaRDD;

import data.model.TVDataSet;
import data.model.TVEvent;

public class TVDataSetUtilities <T extends TVEvent>{
	
	/**
	 * Method that returns tv events that have at least been viewed minTimeView
	 * time.
	 * 
	 * @param minTimeView
	 *            The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least
	 *         minTimeView time.
	 */
	public JavaRDD<T> filterByMinTimeView(JavaRDD<T> events, int minTimeView) {
		return events.filter(tvEvent -> tvEvent.getDuration() >= minTimeView);
	}
	
	/**
	 * Method that filters out all the events that occurred before min week and after max week.
	 * @param minWeek The minimum week number.
	 * @param maxWeek The maximum week number.
	 * @return The filtered tv events.
	 */
	public JavaRDD<T> filterByIntervalOfWeek(JavaRDD<T> events, int minWeek, int maxWeek){
		return events.filter(tvEvent -> tvEvent.getWeek() >= minWeek && tvEvent.getWeek() <= maxWeek);
	}
	
	/**
	 * Method that filters out all the events that occurred before min slot and after max slot.
	 * @param minSlot The minimum week number.
	 * @param maxSlot The maximum week number.
	 * @return The filtered tv events.
	 */
	public JavaRDD<T> filterByIntervalOfSlot(JavaRDD<T> events, int minSlot, int maxSlot){
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
	public JavaRDD<T> filterByIntervalOfDay(JavaRDD<T> events, int minDay, int maxDay){
		return filterByIntervalOfSlot(events, (minDay-1)*24 + 1, (maxDay)*24);
	}

}
