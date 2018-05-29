package util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import util.spark.SparkUtilities;

//TODO: This class no longer exists, all its methods are now directly in TVDataSet class.
// The tests here need to be moved to TVDataSetTest and test need to be improved (using fixture).
public class TVDataSetUtilitiesTest {
	
	final static RecsysTVEvent tvEvent1 = new RecsysTVEvent((short) 1, (short) 2,
			(byte) 3, (byte) 4, (byte) 81, 1, 202344, 50880093, 5);
	final static RecsysTVEvent tvEvent2 = new RecsysTVEvent((short) 4, (short) 7,
			(byte) 1, (byte) 6, (byte) 11, 3, 202344, 51122125, 15);
	final static RecsysTVEvent tvEvent3 = new RecsysTVEvent((short) 6, (short) 33,
			(byte) 1, (byte) 4, (byte) 30, 3, 5785, 51097405, 25);

	static RecsysTVDataSet dataSet;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUp() {
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		sc = SparkUtilities.getADefaultSparkContext();
		dataSet = new RecsysTVDataSet(SparkUtilities.<RecsysTVEvent> elementsToJavaRDD(events, sc));
	}

	@Test
	public void filterByMinTimeViewTest() {
		RecsysTVDataSet filtered_0 = (RecsysTVDataSet) dataSet.filterByMinDuration(0);
		assertEquals(3, filtered_0.numberOfTvEvents());
		RecsysTVDataSet filtered_10 = (RecsysTVDataSet) dataSet.filterByMinDuration(10);
		assertEquals(2, filtered_10.numberOfTvEvents());
		RecsysTVDataSet filtered_20 = (RecsysTVDataSet)dataSet.filterByMinDuration(20);
		assertEquals(1, filtered_20.numberOfTvEvents());
		RecsysTVDataSet filtered_30 = (RecsysTVDataSet) dataSet.filterByMinDuration(30);
		assertEquals(0, filtered_30.numberOfTvEvents());
	}
	
	@AfterClass
	public static void tearDownOnce(){
		sc.close();
	}
}
