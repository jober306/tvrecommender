package data.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Before;
import org.junit.Test;

import data.recsys.model.RecsysTVEvent;
import spark.utilities.SparkUtilities;

public class TVDataSetUtilitiesTest {
	
	final RecsysTVEvent tvEvent1 = new RecsysTVEvent((short) 1, (short) 2,
			(byte) 3, (byte) 4, (byte) 81, 1, 202344, 50880093, 5);
	final RecsysTVEvent tvEvent2 = new RecsysTVEvent((short) 4, (short) 7,
			(byte) 1, (byte) 6, (byte) 11, 3, 202344, 51122125, 15);
	final RecsysTVEvent tvEvent3 = new RecsysTVEvent((short) 6, (short) 33,
			(byte) 1, (byte) 4, (byte) 30, 3, 5785, 51097405, 25);

	JavaRDD<RecsysTVEvent> dataSet;
	TVDataSetUtilities<RecsysTVEvent> utilities;

	@Before
	public void setUp() {
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		JavaSparkContext defaultJavaSparkContext = SparkUtilities
				.getADefaultSparkContext();
		dataSet = SparkUtilities
				.<RecsysTVEvent> elementsToJavaRDD(events,
						defaultJavaSparkContext);
		utilities = new TVDataSetUtilities<RecsysTVEvent>();
	}
	
	@Test
	public void filterByMinTimeViewTest() {
		JavaRDD<RecsysTVEvent> filtered_0 = utilities.filterByMinTimeView(dataSet,0);
		assertTrue(filtered_0.count() == 3);
		JavaRDD<RecsysTVEvent> filtered_10 = utilities.filterByMinTimeView(dataSet,10);
		assertTrue(filtered_10.count() == 2);
		JavaRDD<RecsysTVEvent> filtered_20 = utilities.filterByMinTimeView(dataSet,20);
		assertTrue(filtered_20.count() == 1);
		JavaRDD<RecsysTVEvent> filtered_30 = utilities.filterByMinTimeView(dataSet,30);
		assertTrue(filtered_30.count() == 0);
	}

	@Test
	public void filterByIntervalOfWeekTest() {
		JavaRDD<RecsysTVEvent> filtered = utilities.filterByIntervalOfWeek(dataSet,1, 2);
		assertEquals(2, filtered.count());
	}

	@Test
	public void filterByIntervalOfSlotTest() {
		JavaRDD<RecsysTVEvent> filtered = utilities.filterByIntervalOfSlot(dataSet,7, 7);
		assertEquals(1, filtered.count());
	}

	@Test
	public void filterByIntervalOfDayTest() {
		JavaRDD<RecsysTVEvent> filtered = utilities.filterByIntervalOfDay(dataSet,2, 7);
		assertEquals(1, filtered.count());
	}
}
