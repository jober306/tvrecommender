package data;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Abstract class that force the class extending it to implement a minimum of
 * getter methods essential to tv recommendation.
 * 
 * @author Jonathan Bergeron
 *
 */
public abstract class TVEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The time at which the user started watching.
	 */
	protected final LocalDateTime watchTime;

	/**
	 * The id of the watched program.
	 */
	protected final int programId;
	/**
	 * The watched channel id.
	 */
	protected final int channelId;

	/**
	 * user ID: it is the id of the user.
	 */
	protected final int userID;

	/**
	 * event ID: it is the id of the particular instance of a program. It is
	 * unique, but it can span multiple slots.
	 */
	protected final int eventID;

	/**
	 * duration: duration of the view.
	 */
	protected final int duration;

	public TVEvent(LocalDateTime watchTime, int programId, int channelId,
			int userID, int eventId, int duration) {
		this.watchTime = watchTime;
		this.programId = programId;
		this.channelId = channelId;
		this.userID = userID;
		this.eventID = eventId;
		this.duration = duration;
	}

	/**
	 * Getter method for the watch time param.
	 * 
	 * @return the watchTime
	 */
	public LocalDateTime getWatchTime() {
		return watchTime;
	}

	/**
	 * @return the programId
	 */
	public int getProgramId() {
		return programId;
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId() {
		return channelId;
	}

	/**
	 * Getter for the user parameter.
	 * 
	 * @return The user Id.
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * Getter for the event parameter.
	 * 
	 * @return The event Id.
	 */
	public int getEventID() {
		return eventID;
	}

	/**
	 * Getter for the duration parameter.
	 * 
	 * @return The duration of the event.
	 */
	public int getDuration() {
		return duration;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TVEvent)) {
			return false;
		}
		TVEvent tvEvent = (TVEvent) other;
		return new EqualsBuilder().append(eventID, tvEvent.eventID).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(eventID).toHashCode();
	}
}
