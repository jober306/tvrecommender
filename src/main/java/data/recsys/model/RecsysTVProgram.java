package data.recsys.model;

import static data.recsys.utility.RecsysTVDataSetUtilities.getStartTimeFromWeekAndSlot;
import static data.recsys.utility.RecsysTVDataSetUtilities.getEndTimeFromWeekAndSlot;
import data.model.TVProgram;

public class RecsysTVProgram extends TVProgram{
	
	final int genreId;
	final int subGenreId;
	
	public RecsysTVProgram(int week, short slot, int channelId, int programId, int genreId, int subGenreId) {
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
