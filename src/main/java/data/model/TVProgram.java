package data.model;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Class that represents a tv program in the most simple way.
 * It has a start time, an end time and a channel id (the duration is implicitly calculated
 * from the start and end time).
 * @author Jonathan Bergeron
 *
 */
public abstract class TVProgram {

	protected final LocalTime startTime;
	protected final LocalTime endTime;
	protected final Duration duration;
	protected final int channelId;
	
	/**
	 * Constructor of the TVProgram class, to be accessed only by the children of this class.
	 * @param startTime The start time of the tv program.
	 * @param endTime The end time of the tv program.
	 * @param channelId The channel id of the tv program.
	 */
	public TVProgram(LocalTime startTime, LocalTime endTime, int channelId){
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = Duration.between(startTime, endTime);
		this.channelId = channelId;
	}
	
	/**
	 * @return the startTime
	 */
	public LocalTime getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public LocalTime getEndTime() {
		return endTime;
	}

	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId() {
		return channelId;
	}
}
