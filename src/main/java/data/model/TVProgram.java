package data.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
/**
 * Class that represents a tv program in the most simple way.
 * It has a start time, an end time and a channel id (the duration is implicitly calculated
 * from the start and end time).
 * @author Jonathan Bergeron
 *
 */
public abstract class TVProgram implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected final LocalDateTime startTime;
	protected final LocalDateTime endTime;
	protected final Duration duration;
	protected final int channelId;
	protected final int programId;

	/**
	 * Constructor of the TVProgram class, to be accessed only by the children of this class.
	 * @param startTime The start time of the tv program.
	 * @param endTime The end time of the tv program.
	 * @param channelId The channel id of the tv program.
	 * @param programId The id of this program.
	 */
	public TVProgram(LocalDateTime startTime, LocalDateTime endTime, int channelId, int programId){
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = Duration.between(startTime, endTime);
		this.channelId = channelId;
		this.programId = programId;
	}
	
	/**
	 * @return the startTime
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public LocalDateTime getEndTime() {
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
	
	/**
	 * @return the programId
	 */
	public int getProgramId() {
		return programId;
	}
}
