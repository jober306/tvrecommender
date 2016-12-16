package data.recsys.model;

import static data.recsys.utility.RecsysUtilities.getStartTimeFromWeekAndSlot;

import java.io.Serializable;

import static data.recsys.utility.RecsysUtilities.getEndTimeFromWeekAndSlot;
import data.model.TVProgram;

/**
 * Class that represents a tv program from the recsys data set.
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVProgram extends TVProgram implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The genre id of this program.
	 */
	final int genreId;
	
	/**
	 * The subgenre id of this program.
	 */
	final int subGenreId;
	
	/**
	 * Constructor of recsys tv program class. It calculates its start time and end time from
	 * the week and slot parameter.
	 * @param week The week at which this program was watched by an user
	 * @param slot The slot at which this program was watched by an user.
	 * @param channelId The channel id on which this program was broadcast.
	 * @param programId The id of this program.
	 * @param genreId The genre id of this program.
	 * @param subGenreId The subgenre id of this program.
	 */
	public RecsysTVProgram(short week, short slot, int channelId, int programId, byte genreId, byte subGenreId) {
		super(getStartTimeFromWeekAndSlot(week, slot), getEndTimeFromWeekAndSlot(week, slot), channelId, programId);
		this.genreId = genreId;
		this.subGenreId = subGenreId;
	}
	
	/**
	 * @return the genreId
	 */
	public int getGenreId() {
		return genreId;
	}

	/**
	 * @return the subGenreId
	 */
	public int getSubGenreId() {
		return subGenreId;
	}
}
