package data.recsys;

import static data.recsys.utility.RecsysUtilities.getEndTimeFromWeekAndSlot;
import static data.recsys.utility.RecsysUtilities.getStartTimeFromWeekAndSlot;

import java.io.Serializable;

import data.recsys.utility.RecsysUtilities;
import model.data.TVProgram;

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
	public RecsysTVProgram(short week, short slot, short channelId,
			int programId, byte genreId, byte subGenreId) {
		super(getStartTimeFromWeekAndSlot(week, slot),
				getEndTimeFromWeekAndSlot(week, slot), channelId, programId);
		this.genreId = genreId;
		this.subGenreId = subGenreId;
		this.slot = slot;
	}
	
	/**
	 * Constructor that implicitly construct a recsys tv program from a recsys tv event. 
	 * @param tvEvent The recsys tv event.
	 */
	public RecsysTVProgram(RecsysTVEvent tvEvent) {
		super(getStartTimeFromWeekAndSlot(tvEvent.getWeek(), tvEvent.getSlot()),
				getEndTimeFromWeekAndSlot(tvEvent.getWeek(), tvEvent.getSlot()), tvEvent.channelId(), tvEvent.programID());
		this.genreId = tvEvent.getGenreID();
		this.subGenreId = tvEvent.getSubgenreID();
		this.slot = tvEvent.getSlot();
	}

	/**
	 * @return the genreId
	 */
	public byte genreId() {
		return genreId;
	}

	/**
	 * @return the subGenreId
	 */
	public byte subGenreId() {
		return subGenreId;
	}

	/**
	 * @return the slot
	 */
	public short slot() {
		return slot;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(super.toString());
		s.append("Program ID: ");
		s.append(id());
		s.append("\nBroadcast Time: ");
		s.append(startTime());
		s.append(" to ");
		s.append(endTime());
		s.append("\nGenre: ");
		s.append(RecsysUtilities.getGenreName(genreId));
		s.append("\nSubgenre: ");
		s.append(RecsysUtilities.getSubgenreName(genreId, subGenreId));
		s.append("\n");
		return s.toString();
	}
}
