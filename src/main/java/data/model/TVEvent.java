package data.model;

import java.time.LocalDateTime;

/**
 * Abstract class that force the class extending it to implement a minimum of getter methods essential to tv
 * recommendation.
 * @author Jonathan Bergeron
 *
 */
public abstract class TVEvent {
	
	/**
	 * channel ID: channel id from 1 to 217.
	 */
	protected int channelID;
	
	/**
	 * The time at which the user started watching.
	 */
	protected LocalDateTime watchTime;
	
	/**
	 * user ID: it is the id of the user.
	 */
	protected int userID;
	
	/**
	 * program ID: it is the id of the program. The same program can occur multiple times (e.g. a tv show).
	 */
	protected int programID;
	
	/**
	 * event ID: it is the id of the particular instance of a program. It is unique, but it can span multiple slots.
	 */
	protected int eventID;
	
	/**
	 * duration: duration of the view.
	 */
	protected int duration;
	
	/**
	 * Getter for the channelID param.
	 * @return The channel ID.
	 */
	public int getChannelID(){
		return channelID;
	}
	
	/**
	 * Getter method for the watch time param.
	 * @return the watchTime
	 */
	public LocalDateTime getWatchTime() {
		return watchTime;
	}
	
	/**
	 * Getter for the user parameter.
	 * @return The user Id.
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * Getter for the program parameter.
	 * @return The program Id.
	 */
	public int getProgramID() {
		return programID;
	}

	/**
	 * Getter for the event parameter.
	 * @return The event Id.
	 */
	public int getEventID() {
		return eventID;
	}

	/**
	 * Getter for the duration parameter.
	 * @return The duration of the event.
	 */
	public int getDuration() {
		return duration;
	}
	
	
	
	@Override
	abstract public boolean equals(Object other);
	@Override
	abstract public int hashCode();
}
