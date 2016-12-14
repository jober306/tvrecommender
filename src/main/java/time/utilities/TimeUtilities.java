package time.utilities;

import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;

public class TimeUtilities {
	
	public static boolean isDateTimeBetweenInclusive(ChronoLocalDateTime<?> startTime, ChronoLocalDateTime<?> endTime, ChronoLocalDateTime<?> targetTime){
		return !targetTime.isBefore(startTime) && !targetTime.isAfter(endTime);
	}
	
	public static boolean isTimeBetweenInclusive(LocalTime startTime, LocalTime endTime, LocalTime targetTime){
		return !targetTime.isBefore(startTime) && !targetTime.isAfter(endTime);
	}
}
