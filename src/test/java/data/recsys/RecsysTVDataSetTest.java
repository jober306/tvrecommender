package data.recsys;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.recsys.feature.RecsysFeatureExtractor;
import model.DistributedUserItemMatrix;
import model.LocalUserItemMatrix;
import util.spark.SparkUtilities;

public class RecsysTVDataSetTest {

	final static Map<Integer, double[]> expectedUserItemMatrixValues;
	static{
		expectedUserItemMatrixValues = new HashMap<Integer, double[]>();
		expectedUserItemMatrixValues.put(1, new double[]{1,0});
		expectedUserItemMatrixValues.put(3, new double[]{1,1});
		expectedUserItemMatrixValues.put(5, new double[]{0,1});
	}

	final RecsysTVEvent tvEvent1 = new RecsysTVEvent((short) 1, (short) 2,
			(byte) 3, (byte) 4, (byte) 81, 1, 202344, 50880093, 5);
	final RecsysTVEvent tvEvent2 = new RecsysTVEvent((short) 4, (short) 7,
			(byte) 1, (byte) 6, (byte) 11, 3, 202344, 51122125, 15);
	final RecsysTVEvent tvEvent3 = new RecsysTVEvent((short) 6, (short) 33,
			(byte) 1, (byte) 4, (byte) 30, 3, 5785, 51097405, 25);
	final RecsysTVEvent tvEvent4 = new RecsysTVEvent((short) 6, (short) 33,
			(byte) 1, (byte) 4, (byte) 30, 5, 5785, 51097405, 30);

	RecsysTVDataSet dataSet;

	@Before
	public void setUp() {
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		events.add(tvEvent4);
		JavaSparkContext defaultJavaSparkContext = SparkUtilities
				.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> eventsRDD = SparkUtilities
				.<RecsysTVEvent> elementsToJavaRDD(events,
						defaultJavaSparkContext);
		dataSet = new RecsysTVDataSet(eventsRDD, defaultJavaSparkContext);
	}

	@Test
	public void isNotEmptyTest() {
		assertTrue(!dataSet.isEmpty());
	}

	@Test
	public void isEmptyTest() {
		dataSet.close();
		JavaSparkContext defaultJavaSparkContext = SparkUtilities
				.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> emptyRDD = SparkUtilities
				.<RecsysTVEvent> elementsToJavaRDD(
						new ArrayList<RecsysTVEvent>(), defaultJavaSparkContext);
		RecsysTVDataSet emptyDataSet = new RecsysTVDataSet(emptyRDD,
				defaultJavaSparkContext);
		assertTrue(emptyDataSet.isEmpty());
		emptyDataSet.close();
	}

	@Test
	public void containstTest() {
		assertTrue(dataSet.contains(tvEvent1));
		assertTrue(dataSet.contains(tvEvent2));
		assertTrue(dataSet.contains(tvEvent3));
		assertTrue(dataSet.contains(tvEvent4));
	}

	@Test
	public void getAllUserIdsTest() {
		List<Integer> userIds = dataSet.getAllUserIds();
		assertTrue(userIds.contains(tvEvent1.getUserID()));
		assertTrue(userIds.contains(tvEvent2.getUserID()));
		assertTrue(userIds.contains(tvEvent4.getUserID()));
	}

	@Test
	public void getAllProgramIdsTest() {
		List<Integer> programIds = dataSet.getAllProgramIds();
		assertTrue(programIds.contains(tvEvent1.getProgramId()));
		assertTrue(programIds.contains(tvEvent2.getProgramId()));
	}

	@Test
	public void getAllEventIdsTest() {
		List<Integer> eventIds = dataSet.getAllEventIds();
		assertTrue(eventIds.contains(tvEvent1.getEventID()));
		assertTrue(eventIds.contains(tvEvent2.getEventID()));
	}

	@Test
	public void getNumberOfEntitiesTest() {
		int numberOfUsers = dataSet.getNumberOfUsers();
		int numberOfPrograms = dataSet.getNumberOfTvShows();
		int numberOfEvents = dataSet.count();
		assertEquals(3, numberOfUsers);
		assertEquals(2, numberOfPrograms);
		assertEquals(4, numberOfEvents);
	}

	@Test
	public void getProgramIndexesSeenByUserTest() {
		List<Integer> programsSeenByUser1 = dataSet
				.getTvProgramSeenByUser(1);
		assertEquals(1, programsSeenByUser1.size());
		assertTrue(programsSeenByUser1.contains(tvEvent1.getProgramId()));
		List<Integer> programsSeenByUser3 = dataSet
				.getTvProgramSeenByUser(3);
		assertEquals(2, programsSeenByUser3.size());
		assertTrue(programsSeenByUser3.contains(tvEvent2.getProgramId()));
		assertTrue(programsSeenByUser3.contains(tvEvent3.getProgramId()));
		List<Integer> programsSeenByUser4 = dataSet.getTvProgramSeenByUser(5);
		assertTrue(programsSeenByUser4.contains(tvEvent4.getProgramId()));
	}

	@Test
	public void getProgramIndexesSeenByUserNotExistingTest() {
		List<Integer> userNotExisting = dataSet.getTvProgramSeenByUser(-1);
		assertEquals(0, userNotExisting.size());
	}

	@Test
	public void splitDataRandomlyTest() {
		createBiggerDataSet(42);
		double[] ratios = { 0.17, 0.43, 0.40 };
		List<RecsysTVDataSet> splittedDataSet = dataSet.splitTVEventsRandomly(ratios).stream()
				.map(tvDataSet -> (RecsysTVDataSet) tvDataSet)
				.collect(Collectors.toList());
		JavaRDD<RecsysTVEvent> split0 = splittedDataSet.get(0).getEventsData();
		JavaRDD<RecsysTVEvent> split1 = splittedDataSet.get(1).getEventsData();
		JavaRDD<RecsysTVEvent> split2 = splittedDataSet.get(2).getEventsData();
		assertTrue(split0.intersection(split1).intersection(split2).isEmpty());
		assertTrue(split0.union(split1).union(split2).count() == 42);
	}

	private void createBiggerDataSet(int dataSetSize) {
		dataSet.close();
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		for (int i = 0; i < dataSetSize; i++) {
			events.add(new RecsysTVEvent((short) 1, (short) 2, (byte) 3,
					(byte) 4, (byte) 81, 1 + i, 202344 + i, 50880093 + i, 5));
		}
		JavaSparkContext defaultJavaSparkContext = SparkUtilities
				.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> eventsRDD = SparkUtilities
				.<RecsysTVEvent> elementsToJavaRDD(events,
						defaultJavaSparkContext);
		dataSet = new RecsysTVDataSet(eventsRDD, defaultJavaSparkContext);
	}

	@Test
	public void convertDataSetToMLlibRatingsTest() {
		JavaRDD<Rating> ratings = dataSet.convertToMLlibRatings();
		assertTrue(ratings.count() == 4);
		final List<Integer> expectedUserIds = dataSet.getAllUserIds();
		final List<Integer> expectedProgramIds = dataSet.getAllProgramIds();
		ratings.foreach(rating -> {
			assertTrue(expectedUserIds.contains(rating.user()));
			assertTrue(expectedProgramIds.contains(rating.product()));
			assertTrue(rating.rating() == 1);
		});
	}

	@Test
	public void convertToDistributedMatrixTest() {
		DistributedUserItemMatrix R = dataSet.convertToDistUserItemMatrix();
		List<Integer> userIds = dataSet.getAllUserIds();
		for (int userId : userIds) {
			int mappedId = dataSet.getMappedUserID(userId);
			assertArrayEquals(expectedUserItemMatrixValues.get(userId), R.getRow(mappedId)
					.toArray(), 0.0d);
		}
	}
	
	@Test
	public void convertToLocalMatrixTest(){
		LocalUserItemMatrix R = dataSet.convertToLocalUserItemMatrix();
		List<Integer> userIds = dataSet.getAllUserIds();
		for (int userId : userIds) {
			int mappedId = dataSet.getMappedUserID(userId);
			assertArrayEquals(expectedUserItemMatrixValues.get(userId), R.getRow(mappedId)
					.toArray(), 0.0d);
		}

	}

	@Test
	public void getContentMatrixTest() {
		IndexedRowMatrix C = dataSet.getContentMatrix(RecsysFeatureExtractor
				.getInstance());
		assertEquals(2, C.rows().count());
		assertEquals(2, C.numRows());
		assertEquals(4, C.numCols());
	}

	@After
	public void tearDown() {
		dataSet.close();
	}
}
