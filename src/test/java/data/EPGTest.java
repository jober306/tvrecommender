package data;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import util.spark.SparkUtilities;

public class EPGTest {

	static EPGMock epg;
	static JavaRDD<TVProgramMock> tvPrograms;
	static JavaSparkContext sc;

	/**
	 * Nikola Tesla was born this day!
	 */
	static LocalDateTime baseTime = LocalDateTime.of(1856, 7, 10, 0, 0);

	private static TVProgramMock mock11 = new TVProgramMock(baseTime,
			baseTime.plusHours(1), 1, 1);
	private static TVProgramMock mock13 = new TVProgramMock(
			baseTime.plusHours(1), baseTime.plusMinutes(90), 1, 3);
	private static TVProgramMock mock12 = new TVProgramMock(
			baseTime.plusMinutes(90), baseTime.plusMinutes(150), 1, 2);
	private static TVProgramMock mock23 = new TVProgramMock(baseTime,
			baseTime.plusMinutes(30), 2, 3);
	private static TVProgramMock mock22 = new TVProgramMock(
			baseTime.plusMinutes(30), baseTime.plusMinutes(90), 2, 2);
	private static TVProgramMock mock25 = new TVProgramMock(
			baseTime.plusMinutes(90), baseTime.plusMinutes(120), 2, 5);
	private static TVProgramMock mock26 = new TVProgramMock(
			baseTime.plusMinutes(120), baseTime.plusMinutes(150), 2, 6);
	private static TVProgramMock mock32 = new TVProgramMock(baseTime,
			baseTime.plusMinutes(60), 3, 2);
	private static TVProgramMock mock34 = new TVProgramMock(
			baseTime.plusMinutes(60), baseTime.plusMinutes(150), 3, 4);
	private static TVProgramMock mock44 = new TVProgramMock(baseTime,
			baseTime.plusMinutes(90), 4, 4);
	private static TVProgramMock mock46 = new TVProgramMock(
			baseTime.plusMinutes(90), baseTime.plusMinutes(120), 4, 6);
	private static TVProgramMock mock45 = new TVProgramMock(
			baseTime.plusMinutes(120), baseTime.plusMinutes(150), 4, 5);

	@BeforeClass
	public static void setUp() {
		createTVPrograms();
		epg = new EPGMock(tvPrograms, sc);
	}

	@Test
	public void getListProgramsAtWatchTimeNoProgramEndingTest() {
		List<TVProgramMock> results = epg.getListProgramsAtWatchTime(baseTime
				.plusMinutes(55));
		int expectedSize = 4;
		List<TVProgramMock> expectedResults = Arrays.asList(mock11, mock22,
				mock32, mock44);
		assertEquals(expectedSize, results.size());
		assertEquals(results, expectedResults);
	}

	@Test
	public void getListProgramAtWatchTimeWithProgramEndingTest() {
		List<TVProgramMock> results = epg.getListProgramsAtWatchTime(baseTime
				.plusMinutes(90));
		int expectedSize = 4;
		List<TVProgramMock> expectedResults = Arrays.asList(mock12, mock25,
				mock34, mock46);
		assertEquals(expectedSize, results.size());
		assertEquals(expectedResults, results);
	}

	@Test
	public void getListProgramBetweenTimeNoProgramEnding() {
		List<TVProgramMock> results = epg.getListProgramsBetweenTimes(
				baseTime.plusMinutes(20), baseTime.plusMinutes(25));
		int expectedSize = 4;
		List<TVProgramMock> expectedResults = Arrays.asList(mock11, mock23,
				mock32, mock44);
		assertEquals(expectedSize, results.size());
		assertEquals(expectedResults, results);
	}

	@Test
	public void getListProgramBetweenTimeWithProgramEnding() {
		List<TVProgramMock> results = epg.getListProgramsBetweenTimes(
				baseTime.plusMinutes(30), baseTime.plusMinutes(90));
		int expectedSize = 6;
		List<TVProgramMock> expectedResults = Arrays.asList(mock11, mock13,
				mock22, mock32, mock34, mock44);
		assertEquals(expectedSize, results.size());
		assertEquals(expectedResults, results);
	}

	@Test
	public void getListProgramBetweenTimeNoprogram() {
		List<TVProgramMock> results = epg.getListProgramsBetweenTimes(
				baseTime.plusDays(3), baseTime.plusDays(3));
		assertEquals(0, results.size());

	}

	@AfterClass
	public static void tearDown() {
		sc.stop();
		epg = null;
		tvPrograms = null;
		sc = null;
	}

	static private void createTVPrograms() {
		List<TVProgramMock> tvProgramMocks = new ArrayList<TVProgramMock>();
		tvProgramMocks.add(mock11);
		tvProgramMocks.add(mock13);
		tvProgramMocks.add(mock12);
		tvProgramMocks.add(mock23);
		tvProgramMocks.add(mock22);
		tvProgramMocks.add(mock25);
		tvProgramMocks.add(mock26);
		tvProgramMocks.add(mock32);
		tvProgramMocks.add(mock34);
		tvProgramMocks.add(mock44);
		tvProgramMocks.add(mock46);
		tvProgramMocks.add(mock45);
		sc = SparkUtilities.getADefaultSparkContext();
		tvPrograms = SparkUtilities.elementsToJavaRDD(tvProgramMocks, sc);
	}
}
