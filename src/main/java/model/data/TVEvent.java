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
	final int hashCode;

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
	protected final int watchDuration;

	public TVEvent(LocalDateTime watchTime, P program, U user, int eventId, int watchDuration) {
		this.watchTime = watchTime;
		this.program = program;
		this.user = user;
		this.eventID = eventId;
		this.watchDuration = watchDuration;
		this.hashCode = new HashCodeBuilder().append(watchTime)
				.append(program)
				.append(user)
				.append(eventID)
				.append(watchDuration)
				.hashCode();
	}

	/**
	 * Getter method for the watch time param.
	 * 
	 * @return the watchTime
	 */
	public LocalDateTime watchTime() {
		return watchTime;
	}

	/**
	 * @return the programId
	 */
	public P program() {
		return program;
	}
	
	public int programID(){
		return program.id();
	}

	/**
	 * @return the channelId
	 */
	public short channelId() {
		return program.channelId();
	}
	
	public U user(){
		return user;
	}
	
	/**
	 * Getter for the user parameter.
	 * 
	 * @return The user Id.
	 */
	public int userID() {
		return user.id();
	}

	/**
	 * Getter for the event parameter.
	 * 
	 * @return The event Id.
	 */
	public int eventID() {
		return eventID;
	}

	/**
	 * Getter for the duration parameter.
	 * 
	 * @return The duration of the event.
	 */
	public int watchDuration() {
		return watchDuration;
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
		return new EqualsBuilder().append(watchTime, tvEvent.watchTime)
				.append(program, tvEvent.program)
				.append(user, tvEvent.user)
				.append(eventID, tvEvent.eventID)
				.append(watchDuration, tvEvent.watchDuration)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

}
