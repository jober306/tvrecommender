package data.model;

import java.time.LocalDateTime;

public class TVProgramMock extends TVProgram {

	private static final long serialVersionUID = 1L;

	public TVProgramMock(LocalDateTime startTime, LocalDateTime endTime,
			int channelId, int programId) {
		super(startTime, endTime, channelId, programId);
	}
}
