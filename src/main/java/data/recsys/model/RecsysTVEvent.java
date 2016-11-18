package data.recsys.model;

import java.io.Serializable;

import data.model.TVEvent;

/**
 * Class modeling a tv event of the recsys dataset.
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVEvent extends TVEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * channel ID: channel id from 1 to 217.
	 */
	short channelID;
	
	/**
	 * slot: hour inside the week relative to the start of the view, from 1 to 24*7 = 168.
	 */
	short slot;

	/**
	 * week: week from 1 to 19. Weeks 14 and 19 should not be used because they contain errors.
	 */
	byte week;
	
	/**
	 * genre ID: it is the id of the genre, form 1 to 8.
	 */
	byte genreID;
	
	/**
	 * subGenre ID: it is the id of the subgenre, from 1 to 114.
	 */
	byte subgenreID;
	
	/**
	 * user ID: it is the id of the user.
	 */
	int userID;
	
	/**
	 * program ID: it is the id of the program. The same program can occur multiple times (e.g. a tv show).
	 */
	int programID;
	
	/**
	 * event ID: it is the id of the particular instance of a program. It is unique, but it can span multiple slots.
	 */
	int eventID;
	
	/**
	 * duration: duration of the view.
	 */
	int duration;
	
	/**
	 * Constructor of a Recsys tv event. See documentation about attributes of this class for param documentation.
	 */
	public RecsysTVEvent(short channelID, short slot, byte week, byte genreID, byte subgenreID,
			int userID, int programID, int eventID, int duration){
		this.channelID = channelID;
		this.slot = slot;
		this.week = week;
		this.genreID = genreID;
		this.subgenreID = subgenreID;
		this.userID = userID;
		this.programID = programID;
		this.eventID = eventID;
		this.duration = duration;
	}
	
	/**
	 * Getter for the channelID param.
	 * @return The channel ID.
	 */
	public short getChannelID(){
		return channelID;
	}
	
	/**
	 * Getter for the slot parameter.
	 * @return The slot of the tv event.
	 */
	public short getSlot() {
		return slot;
	}

	/**
	 * Getter for the week parameter
	 * @return The week of the tv event.
	 */
	public byte getWeek() {
		return week;
	}

	/**
	 * Getter for the genre parameter.
	 * @return The genre Id.
	 */
	public byte getGenreID() {
		return genreID;
	}

	/**
	 * Getter for the subgenre parameter.
	 * @return The subgenre Id.
	 */
	public byte getSubgenreID() {
		return subgenreID;
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
	
	/**
	 * Method that overrides the equals method. Two RecsysTVEvents are considered equals if all of their
	 * attributes are equals.
	 */
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof RecsysTVEvent))return false;
	    RecsysTVEvent tvEvent = (RecsysTVEvent)other;
	    return channelID == tvEvent.getChannelID() && slot == tvEvent.getSlot() && week == tvEvent.getWeek() &&
	    		genreID == tvEvent.getGenreID() && subgenreID == tvEvent.getSubgenreID() && userID == tvEvent.getUserID() &&
	    		programID == tvEvent.getProgramID() && eventID == tvEvent.getEventID() && duration == tvEvent.getDuration();
	}
	
	/**
	 * Method that overrides the hashCode method. It uses only the userID and the eventID to create the hash code of this object.
	 */
	@Override
	public int hashCode(){
		return new Integer(userID).hashCode() + new Integer(eventID).hashCode();
	}
	
	/**
	 * Method that overrides the toString method. It return a string with all the formated information
	 * about this recsys tv event.
	 */
	@Override
	public String toString(){
		String tvEventStr = "";
		tvEventStr += "Channel ID: " + channelID + "\n";
		tvEventStr += "Slot: " + slot + "\n";
		tvEventStr += "Week: " + week + "\n";
		tvEventStr += "Genre ID: " + genreID + "\n";
		tvEventStr += "Subgenre ID" + subgenreID + "\n";
		tvEventStr += "User ID: " + userID + "\n";
		tvEventStr += "Event ID: " + eventID + "\n";
		tvEventStr += "Duration: " + duration + "\n";
		return tvEventStr;
	}
}
