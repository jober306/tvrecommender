package util.time;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalDateTimeTest {
	
	LocalDateTimeDTO minTime;
	LocalDateTimeDTO middleTime;
	LocalDateTimeDTO maxTime;
	
	@Before
	public void setUp(){
		minTime = new LocalDateTimeDTO(LocalDateTime.of(1990, 10, 11, 0, 0));
		middleTime = new LocalDateTimeDTO(LocalDateTime.of(1990, 10, 22,0 , 0));
		maxTime = new LocalDateTimeDTO(LocalDateTime.of(2000, 1,1,0,0));
	}
	
	@Test
	public void minTimeIsBeforeMiddleTimeTest(){
		LocalDateTimeDTO expectedMinTime = minTime;
		LocalDateTimeDTO actualMinTime = LocalDateTimeDTO.min(minTime, middleTime);
		LocalDateTimeDTO actualMinTime2 = LocalDateTimeDTO.min(middleTime, minTime);
		assertTrue(expectedMinTime.equals(actualMinTime));
		assertTrue(expectedMinTime.equals(actualMinTime2));
	}
	
	@Test
	public void minTimeIsBeforeMaxTimeTest(){
		LocalDateTimeDTO expectedMinTime = minTime;
		LocalDateTimeDTO actualMinTime = LocalDateTimeDTO.min(minTime, maxTime);
		LocalDateTimeDTO actualMinTime2 = LocalDateTimeDTO.min(maxTime, minTime);
		assertTrue(expectedMinTime.equals(actualMinTime));
		assertTrue(expectedMinTime.equals(actualMinTime2));
	}
	
	@Test
	public void maxTimeIsAfterMidTime(){
		LocalDateTimeDTO expectedMaxTime = maxTime;
		LocalDateTimeDTO actualMaxTime = LocalDateTimeDTO.max(maxTime, middleTime);
		LocalDateTimeDTO actualMaxTime2 = LocalDateTimeDTO.max(middleTime, maxTime);
		assertTrue(expectedMaxTime.equals(actualMaxTime));
		assertTrue(expectedMaxTime.equals(actualMaxTime2));
	}
	
	@After
	public void tearDown(){
		minTime = null;
		middleTime = null;
		maxTime = null;
	}
}
