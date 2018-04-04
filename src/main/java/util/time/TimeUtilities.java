package util.time;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;

public class TimeUtilities implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
