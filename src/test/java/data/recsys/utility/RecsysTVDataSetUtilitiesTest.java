package data.recsys.utility;

import static data.recsys.utility.RecsysUtilities.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.RecsysTVEvent;
import util.spark.SparkUtilities;

public class RecsysTVDataSetUtilitiesTest {
	
	
	static final RecsysTVEvent tvEvent1 = new RecsysTVEvent((short) 1, (short) 2,
			(byte) 3, (byte) 4, (byte) 81, 1, 202344, 50880093, 5);
	static final RecsysTVEvent tvEvent2 = new RecsysTVEvent((short) 4, (short) 7,
			(byte) 1, (byte) 6, (byte) 11, 3, 202344, 51122125, 15);
	static final RecsysTVEvent tvEvent3 = new RecsysTVEvent((short) 6, (short) 33,
			(byte) 1, (byte) 4, (byte) 30, 3, 5785, 51097405, 25);
	
	static JavaRDD<RecsysTVEvent> dataSet;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUpOnce() {
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		sc = SparkUtilities.getADefaultSparkContext();
		dataSet = SparkUtilities.<RecsysTVEvent> elementsToJavaRDD(events, sc);
	}

	@Test
	public void filterByIntervalOfWeekTest() {
		JavaRDD<RecsysTVEvent> filtered = filterByIntervalOfWeek(dataSet,1, 2);
		assertEquals(2, filtered.count());
	}

	@Test
	public void filterByIntervalOfSlotTest() {
		JavaRDD<RecsysTVEvent> filtered = filterByIntervalOfSlot(dataSet,7, 7);
		assertEquals(1, filtered.count());
	}

	@Test
	public void filterByIntervalOfDayTest() {
		JavaRDD<RecsysTVEvent> filtered = filterByIntervalOfDay(dataSet,2, 7);
		assertEquals(1, filtered.count());
	}
	
	@AfterClass
	public static void tearDownOnce(){
		sc.close();
	}
	
	@Test
	public void testResourceFileExists(){
		InputStream stream = RecsysTVDataSetUtilitiesTest.class.getResourceAsStream(RecsysUtilities.GENRE_SUBGENRE_MAPPING_PATH);
		assertTrue(stream != null);
	}
	
	@Test
	public void testMapLoadingCorrectly(){
		assertTrue(RecsysUtilities.isGenreSubgenreMapNotEmpty());
	}
	
	@Test
	public void testGetGenreSubGenreMethod(){
		assertTrue(RecsysUtilities.getGenreName((byte)3).equals("movie"));
		assertTrue(RecsysUtilities.getGenreName((byte)1).equals("kids_and_music"));
		assertTrue(RecsysUtilities.getGenreName((byte)4).equals("society"));
		assertTrue(RecsysUtilities.getSubgenreName((byte) 2, (byte) 14).equals("skiing"));
		assertTrue(RecsysUtilities.getSubgenreName((byte) 6, (byte) 90).equals("economics"));
	}
}
