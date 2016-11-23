package data.recsys.utility;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Before;
import org.junit.Test;

import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import spark.utilities.SparkUtilities;

public class RecsysTVDataSetUtilitiesTest {
	
	
	final RecsysTVEvent tvEvent1 = new RecsysTVEvent((short) 1, (short) 2,
			(byte) 3, (byte) 4, (byte) 81, 1, 202344, 50880093, 5);
	final RecsysTVEvent tvEvent2 = new RecsysTVEvent((short) 4, (short) 7,
			(byte) 1, (byte) 6, (byte) 11, 3, 202344, 51122125, 15);
	final RecsysTVEvent tvEvent3 = new RecsysTVEvent((short) 6, (short) 33,
			(byte) 1, (byte) 4, (byte) 30, 3, 5785, 51097405, 25);

	RecsysTVDataSet dataSet;

	@Before
	public void setUp() {
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		JavaSparkContext defaultJavaSparkContext = SparkUtilities
				.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> eventsRDD = SparkUtilities
				.<RecsysTVEvent> elementsToJavaRDD(events,
						defaultJavaSparkContext);
		dataSet = new RecsysTVDataSet(eventsRDD, defaultJavaSparkContext, true);
	}
	
	@Test
	public void testResourceFileExists(){
		InputStream stream = RecsysTVDataSetUtilitiesTest.class.getResourceAsStream(RecsysTVDataSetUtilities.GENRE_SUBGENRE_MAPPING_PATH);
		assertTrue(stream != null);
	}
	
	@Test
	public void testMapLoadingCorrectly(){
		assertTrue(RecsysTVDataSetUtilities.isGenreSubgenreMapNotEmpty());
	}
	
	@Test
	public void testGetGenreSubGenreMethod(){
		assertTrue(RecsysTVDataSetUtilities.getGenreName((byte)3).equals("movie"));
		assertTrue(RecsysTVDataSetUtilities.getGenreName((byte)1).equals("kids_and_music"));
		assertTrue(RecsysTVDataSetUtilities.getGenreName((byte)4).equals("society"));
		assertTrue(RecsysTVDataSetUtilities.getSubgenreName((byte) 2, (byte) 14).equals("skiing"));
		assertTrue(RecsysTVDataSetUtilities.getSubgenreName((byte) 6, (byte) 90).equals("economics"));
	}
}
