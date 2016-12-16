package data.recsys.model;

import static data.recsys.utility.RecsysTVDataSetUtilities.getStartTimeFromWeekAndSlot;

import java.io.Serializable;

import static data.recsys.utility.RecsysTVDataSetUtilities.getEndTimeFromWeekAndSlot;
import data.model.TVProgram;

public class RecsysTVProgram extends TVProgram implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	final int genreId;
	final int subGenreId;
	
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
