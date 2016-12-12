package data.recsys.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import data.model.TVEvent;

/**
 * Class modeling a tv event of the recsys dataset.
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVEvent extends TVEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * genre ID: it is the id of the genre, form 1 to 8.
	 */
	byte genreID;
	
	/**
	 * subGenre ID: it is the id of the subgenre, from 1 to 114.
	 */
	byte subgenreID;
	
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
	 * Method that overrides the equals method. Two RecsysTVEvents are considered equals if all of their
	 * attributes are equals.
	 */
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof RecsysTVEvent))return false;
	    RecsysTVEvent tvEvent = (RecsysTVEvent)other;
	    return new EqualsBuilder().append(channelID,tvEvent.getChannelID()).
	    		append(slot, tvEvent.getSlot()).
	    		append(week, tvEvent.getWeek()).
	    		append(genreID, tvEvent.getGenreID()). 
	    		append(subgenreID, tvEvent.getSubgenreID()).
	    		append(userID, tvEvent.getUserID()).
	    		append(programID, tvEvent.getProgramID()). 
	    		append(eventID, tvEvent.getEventID()).
	    		append(duration, tvEvent.getDuration()).
	    		isEquals();
	}
	
	/**
	 * Method that overrides the hashCode method. It uses only the userID and the eventID to create the hash code of this object.
	 */
	@Override
	public int hashCode(){
		return new HashCodeBuilder().append(userID).append(eventID).hashCode();
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
