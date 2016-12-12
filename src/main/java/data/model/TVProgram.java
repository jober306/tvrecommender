package data.model;

import java.time.Duration;
import java.time.LocalTime;

public abstract class TVProgram {
	
	LocalTime startTime;
	LocalTime endTime;
	Duration duration;
	int channelId;
}
