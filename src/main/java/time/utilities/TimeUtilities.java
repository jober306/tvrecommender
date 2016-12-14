package time.utilities;

import java.time.chrono.ChronoLocalDateTime;

public class TimeUtilities {
	
	public static boolean isBetweenInclusive(ChronoLocalDateTime<?> startTime, ChronoLocalDateTime<?> endTime, ChronoLocalDateTime<?> targetTime){
		return !targetTime.isBefore(startTime) && !targetTime.isAfter(endTime);
	}
}
