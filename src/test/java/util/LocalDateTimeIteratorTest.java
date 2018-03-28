package util;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class LocalDateTimeIteratorTest {
	
	static final LocalDateTime MY_BIRTHDAY = LocalDateTime.of(1990, 10, 11, 0, 0);
	static final LocalDateTime ONE_MONTHS_OLD = MY_BIRTHDAY.plusDays(7);
	static final LocalDateTime TWO_MONTHS_OLD = MY_BIRTHDAY.plusDays(14);
	static final LocalDateTime THREE_MONTHS_OLD = MY_BIRTHDAY.plusDays(21);
	static final LocalDateTime FOUR_MONTHS_OLD = MY_BIRTHDAY.plusDays(28);
	static final LocalDateTime FIVE_MONTHS_OLD = MY_BIRTHDAY.plusDays(35);
	static final LocalDateTime SIX_MONTHS_OLD = MY_BIRTHDAY.plusDays(42);
	static final Duration ONE_MONTH = Duration.ofDays(7);
	static final Duration MINUS_ONE_MONTH = Duration.ofDays(-7);
	
	static final List<LocalDateTime> EXPECTED_LIST = Arrays.asList(MY_BIRTHDAY, ONE_MONTHS_OLD, TWO_MONTHS_OLD, THREE_MONTHS_OLD, FOUR_MONTHS_OLD, FIVE_MONTHS_OLD, SIX_MONTHS_OLD);
	static final List<LocalDateTime> EXPECTED_INVERSED_LIST = Arrays.asList(SIX_MONTHS_OLD, FIVE_MONTHS_OLD, FOUR_MONTHS_OLD, THREE_MONTHS_OLD, TWO_MONTHS_OLD, ONE_MONTHS_OLD, MY_BIRTHDAY);

	@Test
	public void validTimeIterableTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(MY_BIRTHDAY, SIX_MONTHS_OLD, ONE_MONTH);
		List<LocalDateTime> times = new ArrayList<LocalDateTime>();
		for(LocalDateTime time : timeIt) {
			times.add(time);
		}
		int expectedSize = 7;
		assertEquals(expectedSize, times.size());
		assertEquals(EXPECTED_LIST, times);
	}
	
	@Test
	public void validTimeStreamTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(MY_BIRTHDAY, SIX_MONTHS_OLD, ONE_MONTH);
		List<LocalDateTime> times = timeIt.stream().collect(Collectors.toList());
		int expectedSize = 7;
		assertEquals(expectedSize, times.size());
		assertEquals(EXPECTED_LIST, times);
	}
	
	@Test
	public void validTimeIteratorTest() {
		Iterator<LocalDateTime> timeIt = new LocalDateTimeIterator(MY_BIRTHDAY, SIX_MONTHS_OLD, ONE_MONTH).iterator();
		List<LocalDateTime> times = new ArrayList<LocalDateTime>();
		while(timeIt.hasNext()) {
			times.add(timeIt.next());
		}
		int expectedSize = 7;
		assertEquals(expectedSize, times.size());
		assertEquals(EXPECTED_LIST, times);
	}
	
	@Test
	public void invalidTimeIterableTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(SIX_MONTHS_OLD, MY_BIRTHDAY, ONE_MONTH);
		List<LocalDateTime> times = new ArrayList<LocalDateTime>();
		for(LocalDateTime time : timeIt) {
			times.add(time);
		}
		int expectedSize = 0;
		assertEquals(expectedSize, times.size());
	}
	
	@Test
	public void invalidTimeStreamTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(SIX_MONTHS_OLD, MY_BIRTHDAY, ONE_MONTH);
		List<LocalDateTime> times = timeIt.stream().collect(Collectors.toList());
		int expectedSize = 0;
		assertEquals(expectedSize, times.size());
	}
	
	@Test
	public void invalidTimeIteratorTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(SIX_MONTHS_OLD, MY_BIRTHDAY, ONE_MONTH);
		List<LocalDateTime> times = new ArrayList<LocalDateTime>();
		while(timeIt.hasNext()) {
			times.add(timeIt.next());
		}
		int expectedSize = 0;
		assertEquals(expectedSize, times.size());
	}
	
	@Test
	public void validTimeIterableMinonOneMonthTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(SIX_MONTHS_OLD, MY_BIRTHDAY, MINUS_ONE_MONTH);
		List<LocalDateTime> times = new ArrayList<LocalDateTime>();
		for(LocalDateTime time : timeIt) {
			times.add(time);
		}
		int expectedSize = 7;
		assertEquals(expectedSize, times.size());
		assertEquals(EXPECTED_INVERSED_LIST, times);
	}
	
	@Test
	public void validTimeStreamMinonOneMonthTest() {
		LocalDateTimeIterator timeIt = new LocalDateTimeIterator(SIX_MONTHS_OLD, MY_BIRTHDAY, MINUS_ONE_MONTH);
		List<LocalDateTime> times = timeIt.stream().collect(Collectors.toList());
		int expectedSize = 7;
		assertEquals(expectedSize, times.size());
		assertEquals(EXPECTED_INVERSED_LIST, times);
	}
	
	@Test
	public void validTimeIteratorMinonOneMonthTest() {
		Iterator<LocalDateTime> timeIt = new LocalDateTimeIterator(SIX_MONTHS_OLD, MY_BIRTHDAY, MINUS_ONE_MONTH).iterator();
		List<LocalDateTime> times = new ArrayList<LocalDateTime>();
		while(timeIt.hasNext()) {
			times.add(timeIt.next());
		}
		int expectedSize = 7;
		assertEquals(expectedSize, times.size());
		assertEquals(EXPECTED_INVERSED_LIST, times);
	}
}
