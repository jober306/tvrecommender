package data.recsys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
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
	
	private RecsysTVEvent event;
	
	@Before
	public void setUp(){
		event = new RecsysTVEvent(channelID, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
	}
	
	@Test
	public void constructorAndGetterTest(){
		System.out.println(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
		assertTrue(event != null);
		assertTrue(channelID == event.getChannelId());
		assertTrue(slot == event.getSlot());
		assertTrue(week == event.getWeek());
		assertTrue(genreID == event.getGenreID());
		assertTrue(subgenreID == event.getSubgenreID());
		assertTrue(userID == event.getUserID());
		assertTrue(programID == event.getProgramId());
		assertTrue(eventID == event.getEventID());
		assertTrue(duration == event.getDuration());
		System.out.println(event.getWatchTime().toString());
	}
	
	@Test
	public void watchTimeSetCorrectlyTest(){
		LocalDateTime expectedWatchTime = LocalDateTime.of(1995, 4,16,7,0);
		assertEquals(expectedWatchTime, event.getWatchTime());
	}
	
	@Test
	public void equalsTvEventTest(){
		RecsysTVEvent event2 = new RecsysTVEvent(channelID, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
		assertTrue(event.equals(event2));
		RecsysTVEvent event3 = new RecsysTVEvent((byte)22, slot, week, genreID, subgenreID, userID, programID, eventID, duration);
		assertTrue(event != event3);
	}
	
	@After
	public void tearDown(){
		event = null;
	}
}
