package data.recsys;

import static data.recsys.utility.RecsysUtilities.getEndTimeFromWeekAndSlot;
import static data.recsys.utility.RecsysUtilities.getStartTimeFromWeekAndSlot;

import java.io.Serializable;

import data.TVProgram;

/**
 * Class that represents a tv program from the recsys data set.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVProgram extends TVProgram implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The genre id of this program.
	 */
	final byte genreId;

	/**
	 * The subgenre id of this program.
	 */
	final byte subGenreId;

	/**
	 * The slot of time in which the program occurred.
	 */
	final short slot;

	/**
	 * Constructor of recsys tv program class. It calculates its start time and
	 * end time from the week and slot parameter.
	 * 
	 * @param week
	 *            The week at which this program was watched by an user
	 * @param slot
	 *            The slot at which this program was watched by an user.
	 * @param channelId
	 *            The channel id on which this program was broadcast.
	 * @param programId
	 *            The id of this program.
	 * @param genreId
	 *            The genre id of this program.
	 * @param subGenreId
	 *            The subgenre id of this program.
	 */
	public RecsysTVProgram(short week, short slot, int channelId,
			int programId, byte genreId, byte subGenreId) {
		super(getStartTimeFromWeekAndSlot(week, slot),
				getEndTimeFromWeekAndSlot(week, slot), channelId, programId);
		this.genreId = genreId;
		this.subGenreId = subGenreId;
		this.slot = slot;
	}

	/**
	 * @return the genreId
	 */
	public byte getGenreId() {
		return genreId;
	}

	/**
	 * @return the subGenreId
	 */
	public byte getSubGenreId() {
		return subGenreId;
	}

	/**
	 * @return the slot
	 */
	public short getSlot() {
		return slot;
	}
}
