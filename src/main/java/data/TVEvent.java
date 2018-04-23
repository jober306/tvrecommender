package data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract class that force the class extending it to implement a minimum of
 * getter methods essential to tv recommendation.
 * 
 * @author Jonathan Bergeron
 *
 */
public class TVEvent extends AbstractTVEvent<TVProgram> implements Serializable {

	private static final long serialVersionUID = 1L;

	public TVEvent(LocalDateTime watchTime, TVProgram program,
			int userID, int eventId, int duration) {
		super(watchTime, program, userID, eventId, duration);
	}
}
