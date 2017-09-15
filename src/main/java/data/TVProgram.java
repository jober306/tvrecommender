package data;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class that represents a tv program in the most simple way. It has a start
 * time, an end time and a channel id (the duration is implicitly calculated
 * from the start and end time).
 * 
 * @author Jonathan Bergeron
 *
 */
public abstract class TVProgram implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The start date time of the program. This is considered inclusive.
	 */
	protected final LocalDateTime startTime;

	/**
	 * The end date time of the program. This is considered exclusive.
	 */
	protected final LocalDateTime endTime;

	/**
	 * The duration of the program, it is calculated implicitly with start time
	 * and end time.
	 */
	protected final Duration duration;

	/**
	 * The channel id on which the program is broadcasted.
	 */
	protected final int channelId;

	/**
	 * The id of the prorgram.
	 */
	protected final int programId;

	/**
	 * Constructor of the TVProgram class, to be accessed only by the children
	 * of this class.
	 * 
	 * @param startTime
	 *            The start time of the tv program.
	 * @param endTime
	 *            The end time of the tv program.
	 * @param channelId
	 *            The channel id of the tv program.
	 * @param programId
	 *            The id of this program.
	 */
	public TVProgram(LocalDateTime startTime, LocalDateTime endTime,
			int channelId, int programId) {
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

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof TVProgram)) {
			return false;
		}
		TVProgram tvProgram = (TVProgram) other;
		return new EqualsBuilder().append(startTime, tvProgram.startTime)
				.append(endTime, tvProgram.endTime)
				.append(channelId, tvProgram.channelId)
				.append(programId, tvProgram.programId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(channelId).append(programId)
				.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Program Id: " + programId + "\n");
		s.append("Channel Id: " + channelId + "\n");
		s.append("Start time: " + startTime.toString() + "\n");
		s.append("End Time: " + endTime.toString() + "\n");
		return s.toString();
	}
}