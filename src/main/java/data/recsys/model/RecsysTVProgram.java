package data.recsys.model;

import java.time.LocalDateTime;

import data.model.TVProgram;

public class RecsysTVProgram extends TVProgram{

	public RecsysTVProgram(LocalDateTime startTime, LocalDateTime endTime, int channelId, int genreId, int subGenreId) {
		super(startTime, endTime, channelId);
	}
	
}
