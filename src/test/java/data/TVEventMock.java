package data;

import java.time.LocalDateTime;

public class TVEventMock extends TVEvent {

	private static final long serialVersionUID = 1L;

	public TVEventMock(LocalDateTime watchTime, int programId, int channelId,
			int userID, int eventId, int duration) {
		super(watchTime, programId, channelId, userID, eventId, duration);
	}

}
