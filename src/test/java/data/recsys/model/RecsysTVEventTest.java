package data.recsys.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RecsysTVEventTest {
	
	private final short channelID = 10;
	private final short slot = 152;
	private final byte week = 1;
	private final byte genreID = 3;
	private final byte subgenreID = 20;
	private final int userID = 1029;
	private final int programID = 9021848;
	private final int eventID = 3813;
	private final int duration = 30;
	
	@Test
	public void constructorAndGetterTest(){
		RecsysTVEvent event = new RecsysTVEvent(channelID, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
		assertTrue(event != null);
		assertTrue(channelID == event.getChannelID());
		assertTrue(slot == event.getSlot());
		assertTrue(week == event.getWeek());
		assertTrue(genreID == event.getGenreID());
		assertTrue(subgenreID == event.getSubgenreID());
		assertTrue(userID == event.getUserID());
		assertTrue(programID == event.getProgramID());
		assertTrue(eventID == event.getEventID());
		assertTrue(duration == event.getDuration());
	}
	
	@Test
	public void equalsTvEventTest(){
		RecsysTVEvent event1 = new RecsysTVEvent(channelID, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
		RecsysTVEvent event2 = new RecsysTVEvent(channelID, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
		assertTrue(event1.equals(event2));
		RecsysTVEvent event3 = new RecsysTVEvent((byte)22, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
		assertTrue(event1 != event3);
	}
}
