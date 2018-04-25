package model.data;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A class that represents an abstract tv event.
 * @author Jonathan Bergeron 
 *
 * @param <P> The type of the tv program watched.
 */
public class TVEvent<U extends User, P extends TVProgram> implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * The time at which the user started watching.
	 */
	protected final LocalDateTime watchTime;

	/**
	 * The watched program.
	 */
	protected final P program;

	/**
	 * The user associated with this event.
	 */
	protected final U user;

	/**
	 * event ID: it is the id of the particular instance of a program. It is
	 * unique, but it can span multiple slots.
	 */
	protected final int eventID;

	/**
	 * duration: duration of the view.
	 */
	protected final int duration;

	public TVEvent(LocalDateTime watchTime, P program, U user, int eventId, int duration) {
		this.watchTime = watchTime;
		this.program = program;
		this.user = user;
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
	public P getProgram() {
		return program;
	}
	
	public int getProgramID(){
		return program.programId();
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId() {
		return program.channelId();
	}
	
	public U getUser(){
		return user;
	}
	
	/**
	 * Getter for the user parameter.
	 * 
	 * @return The user Id.
	 */
	public int getUserID() {
		return user.id();
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
		if (!(other instanceof TVEvent<?,?>)) {
			return false;
		}
		TVEvent<?,?> tvEvent = (TVEvent<?,?>) other;
		return new EqualsBuilder().append(eventID, tvEvent.eventID).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(eventID).toHashCode();
	}

}
