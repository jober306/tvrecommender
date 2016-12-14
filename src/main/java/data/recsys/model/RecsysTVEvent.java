package data.recsys.model;
import static data.recsys.utility.RecsysTVDataSetUtilities.getStartTimeFromWeekAndSlot;

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
	 * slot: hour inside the week relative to the start of the view, from 1 to 24*7 = 168.
	 */
	protected int slot;

	/**
	 * week: week from 1 to 19. Weeks 14 and 19 should not be used because they contain errors.
	 */
	protected int week;
	
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
	 * The watch time is initialized implicitly, considering the recsys tv events data set started 
	 */
	public RecsysTVEvent(short channelID, short slot, byte week, byte genreID, byte subgenreID,
			int userID, int programID, int eventID, int duration){
		super(getStartTimeFromWeekAndSlot(week, slot),programID,channelID,userID,eventID,duration);
		this.slot = slot;
		this.week = week;
		this.genreID = genreID;
		this.subgenreID = subgenreID;
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
	    return new EqualsBuilder().append(channelId,tvEvent.getChannelId()).
	    		append(slot, tvEvent.getSlot()).
	    		append(week, tvEvent.getWeek()).
	    		append(genreID, tvEvent.getGenreID()). 
	    		append(subgenreID, tvEvent.getSubgenreID()).
	    		append(userID, tvEvent.getUserID()).
	    		append(programId, tvEvent.getProgramId()). 
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
		tvEventStr += "Channel ID: " + channelId + "\n";
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
