package util.time;

import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;

/**
 * Class that offers time utilities methods related to java 8 time.
 * @author Jonathan Bergeron
 *
 */
public class TimeUtilities {

	/**
	 * Method that checks if a target time is between the inclusive start time and the exclusive end time.
	 * @param startTime The start time inclusively.
	 * @param endTime The end time exclusively.
	 * @param targetTime The target time to check.
	 * @return true if target time is between the inclusive start time and the exclusive end time, false otherwise.
	 */
	public static boolean isDateTimeBetween(ChronoLocalDateTime<?> startTime, ChronoLocalDateTime<?> endTime, ChronoLocalDateTime<?> targetTime){
		return !targetTime.isBefore(startTime) && endTime.isAfter(targetTime);
	}
	
	/**
	 * Method that checks if a target time is between the inclusive start time and the exclusive end time.
	 * @param startTime The start time inclusively.
	 * @param endTime The end time exclusively.
	 * @param targetTime The target time to check.
	 * @return true if target time is between the inclusive start time and the exclusive end time, false otherwise.
	 */
	public static boolean isTimeBetween(LocalTime startTime, LocalTime endTime, LocalTime targetTime){
		return !targetTime.isBefore(startTime) && endTime.isAfter(targetTime);
	}
}
