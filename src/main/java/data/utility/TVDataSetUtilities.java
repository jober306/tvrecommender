package data.utility;

import static time.utilities.TimeUtilities.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.spark.api.java.JavaRDD;

import data.model.TVEvent;

/**
 * Class that offers some general utilities on tv data set objects.
 * @author Jonathan Bergeron
 *
 */
public class TVDataSetUtilities {
	
	/**
	 * Method that returns tv events that have at least been viewed minTimeView
	 * time.
	 * 
	 * @param minTimeView
	 *            The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least
	 *         minTimeView time.
	 */
	public static <T extends TVEvent> JavaRDD<T> filterByMinTimeView(JavaRDD<T> events, int minTimeView) {
		return events.filter(tvEvent -> tvEvent.getDuration() >= minTimeView);
	}
	
	/**
	 * Method that returns tv events that have been started watching between start time inclusively and end time exclusively.
	 * @param events All the tv events.
	 * @param startTime The start time.
	 * @param endTime The end time.
	 * @return The tv events between startTime and endTime.
	 */
	public static <T extends TVEvent> JavaRDD<T> filterByDateTime(JavaRDD<T> events, LocalDateTime startTime,LocalDateTime endTime ){
		return events.filter(tvEvent -> isDateTimeBetween(startTime, endTime, tvEvent.getWatchTime()));
	}
	
	/**
	 * Method that returns tv events that have been started watching between start time inclusively and end time exclusively ignoring
	 * the date. For instance it could filter out all the events that are not between 12h00 and 16h30.
	 * @param events All the tv events.
	 * @param startTime The start time.
	 * @param endTime The end time.
	 * @return The tv events between startTime and endTime.
	 */
	public static <T extends TVEvent> JavaRDD<T> filterByTime(JavaRDD<T> events, LocalTime startTime,LocalTime endTime){
		return events.filter(tvEvent -> isTimeBetween(startTime, endTime, tvEvent.getWatchTime().toLocalTime()));
	}
}
