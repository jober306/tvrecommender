package data.model;

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
	 * slot: hour inside the week relative to the start of the view, from 1 to 24*7 = 168.
	 */
	protected int slot;

	/**
	 * week: week from 1 to 19. Weeks 14 and 19 should not be used because they contain errors.
	 */
	protected int week;
	
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
	 * Getter for the slot parameter.
	 * @return The slot of the tv event.
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * Getter for the week parameter
	 * @return The week of the tv event.
	 */
	public int getWeek() {
		return week;
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
