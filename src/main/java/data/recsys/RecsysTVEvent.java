package data.recsys;

import static data.recsys.utility.RecsysUtilities.getStartTimeFromWeekAndSlot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import data.recsys.utility.RecsysUtilities;
import model.data.TVEvent;
import model.data.User;

/**
 * Class modeling a tv event of the recsys dataset.
 * 
 * @author Jonathan Bergeron
 *
 */
final public class RecsysTVEvent extends TVEvent<User, RecsysTVProgram> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * slot: hour inside the week relative to the start of the view, from 1 to
	 * 24*7 = 168.
	 */
	short slot;

	/**
	 * week: week from 1 to 19. Weeks 14 and 19 should not be used because they
	 * contain errors.
	 */
	short week;

	/**
	 * genre ID: it is the id of the genre, form 1 to 8.
	 */
	byte genreID;

	/**
	 * subGenre ID: it is the id of the subgenre, from 1 to 114.
	 */
	byte subgenreID;

	/**
	 * Constructor of a Recsys tv event. See documentation about attributes of
	 * this class for param documentation. The watch time is initialized
	 * implicitly, considering the recsys tv events data set started
	 */
	public RecsysTVEvent(short channelID, short slot, short week, byte genreID,
			byte subgenreID, int userID, int programID, int eventID, int duration) {
		super(getStartTimeFromWeekAndSlot(week, slot), new RecsysTVProgram(week, slot, channelID, programID, genreID, subgenreID), new User(userID), eventID, duration);
		this.slot = slot;
		this.week = week;
		this.genreID = genreID;
		this.subgenreID = subgenreID;
	}

	/**
	 * Getter for the slot parameter.
	 * 
	 * @return The slot of the tv event.
	 */
	public short getSlot() {
		return slot;
	}

	/**
	 * Getter for the week parameter
	 * 
	 * @return The week of the tv event.
	 */
	public short getWeek() {
		return week;
	}

	/**
	 * Getter for the genre parameter.
	 * 
	 * @return The genre Id.
	 */
	public byte getGenreID() {
		return genreID;
	}

	/**
	 * Getter for the subgenre parameter.
	 * 
	 * @return The subgenre Id.
	 */
	public byte getSubgenreID() {
		return subgenreID;
	}
	
	/**
	 * Method that return all the possible values that the slot field can take.
	 * @return A list containing all the possible values.
	 */
	public static List<Short> getAllPossibleSlots(){
		List<Short> allPossibleSlots = new ArrayList<Short>(168);
		for(int i = 1; i <= 168; i++){
			allPossibleSlots.add((short) i);
		}
		return allPossibleSlots;
	}

	/**
	 * Method that overrides the equals method. Two RecsysTVEvents are
	 * considered equals if all of their attributes are equals.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof RecsysTVEvent))
			return false;
		RecsysTVEvent tvEvent = (RecsysTVEvent) other;
		return new EqualsBuilder().append(channelId(), tvEvent.channelId())
				.append(slot, tvEvent.getSlot())
				.append(week, tvEvent.getWeek())
				.append(genreID, tvEvent.getGenreID())
				.append(subgenreID, tvEvent.getSubgenreID())
				.append(user.id(), tvEvent.userID())
				.append(program, tvEvent.program())
				.append(eventID, tvEvent.eventID())
				.append(watchDuration, tvEvent.watchDuration()).isEquals();
	}

	/**
	 * Method that overrides the hashCode method. It uses only the userID and
	 * the eventID to create the hash code of this object.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(user.id()).append(eventID).hashCode();
	}

	/**
	 * Method that overrides the toString method. It return a string with all
	 * the formated information about this recsys tv event.
	 */
	@Override
	public String toString() {
		String tvEventStr = "";
		tvEventStr += "Channel ID: " + channelId() + "\n";
		tvEventStr += "Slot: " + slot + "\n";
		tvEventStr += "Week: " + week + "\n";
		tvEventStr += "Genre ID: " + RecsysUtilities.getGenreName(genreID) + "\n";
		tvEventStr += "Subgenre ID" + RecsysUtilities.getSubgenreName(genreID, subgenreID) + "\n";
		tvEventStr += "User ID: " + user.id() + "\n";
		tvEventStr += "Event ID: " + eventID + "\n";
		tvEventStr += "Duration: " + watchDuration + "\n";
		return tvEventStr;
	}
}
