package data.recsys.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.Rating;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spark.utilities.SparkUtilities;

public class RecsysTVDataSetTest {
	
	final RecsysTVEvent tvEvent1 = new RecsysTVEvent((short)1,(short)2,(byte)3,(byte)4,(byte)81,1,202344,50880093,5);
	final RecsysTVEvent tvEvent2 = new RecsysTVEvent((short)4,(short)7,(byte)1,(byte)6,(byte)11,3,5785,51122125,15);
	final RecsysTVEvent tvEvent3 = new RecsysTVEvent((short)6,(short)33,(byte)1,(byte)4,(byte)30,3,5785,51097405,25);
	
	RecsysTVDataSet dataSet;
	
	@Before
	public void setUp(){
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		JavaSparkContext defaultJavaSparkContext = SparkUtilities.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> eventsRDD = SparkUtilities.<RecsysTVEvent>elementsToJavaRDD(events, defaultJavaSparkContext);
		dataSet = new RecsysTVDataSet(eventsRDD, defaultJavaSparkContext);
	}
	
	@Test
	public void isNotEmptyTest(){
		assertTrue(!dataSet.isEmpty());
	}
	
	@Test
	public void isEmptyTest(){
		dataSet.close();
		JavaSparkContext defaultJavaSparkContext = SparkUtilities.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> emptyRDD = SparkUtilities.<RecsysTVEvent>elementsToJavaRDD(new ArrayList<RecsysTVEvent>(), defaultJavaSparkContext);
		RecsysTVDataSet emptyDataSet = new RecsysTVDataSet(emptyRDD, defaultJavaSparkContext);
		assertTrue(emptyDataSet.isEmpty());
		emptyDataSet.close();
	}
	
	@Test
	public void containstTest(){
		assertTrue(dataSet.contains(tvEvent1));
		assertTrue(dataSet.contains(tvEvent2));
		assertTrue(dataSet.contains(tvEvent3));
	}
	
	@Test
	public void getAllUserIdsTest(){
		List<Integer> userIds = dataSet.getAllUserIds();
		assertTrue(userIds.contains(tvEvent1.getUserID()));
		assertTrue(userIds.contains(tvEvent2.getUserID()));
	}
	
	@Test
	public void getAllProgramIdsTest(){
		List<Integer> programIds = dataSet.getAllProgramIds();
		assertTrue(programIds.contains(tvEvent1.getProgramID()));
		assertTrue(programIds.contains(tvEvent2.getProgramID()));
	}
	
	@Test
	public void getAllEventIdsTest(){
		List<Integer> eventIds = dataSet.getAllEventIds();
		assertTrue(eventIds.contains(tvEvent1.getEventID()));
		assertTrue(eventIds.contains(tvEvent2.getEventID()));
	}
	
	@Test
	public void convertDataSetToMLlibRatingsTest(){
		JavaRDD<Rating> ratings = dataSet.convertToMLlibRatings();
		assertTrue(ratings.count()==3);
		List<Integer> expectedIDs = Arrays.asList(0,1,2);
		ratings.foreach(rating -> {
			assertTrue(expectedIDs.contains(rating.user()));
			assertTrue(expectedIDs.contains(rating.product()));
			assertTrue(rating.rating()==1);
		});
	}
	
	@Test
	public void filterByMinTimeViewTest(){
		JavaRDD<RecsysTVEvent> filtered_0 = dataSet.filterByMinTimeView(0);
		assertTrue(filtered_0.count() == 3);
		JavaRDD<RecsysTVEvent> filtered_10 = dataSet.filterByMinTimeView(10);
		assertTrue(filtered_10.count() == 2);
		JavaRDD<RecsysTVEvent> filtered_20 = dataSet.filterByMinTimeView(20);
		assertTrue(filtered_20.count() == 1);
		JavaRDD<RecsysTVEvent> filtered_30 = dataSet.filterByMinTimeView(30);
		assertTrue(filtered_30.count() == 0);
	}
	
	@Test
	public void getNumberOfEntitiesTest(){
		int numberOfUsers = dataSet.getNumberOfUsers();
		int numberOfPrograms = dataSet.getNumberOfItems();
		int numberOfEvents = dataSet.getNumberOfEvents();
		assertTrue(numberOfUsers == 2);
		assertTrue(numberOfPrograms == 2);
		assertTrue(numberOfEvents == 3);
	}
	
	@Test
	public void getIndexesCorrespondingToRatiosTest(){
		createBiggerDataSet(100);
		double[] ratios = {0.34,0.21,0.45};
		int[] indexObtained = dataSet.getIndexesCorrespondingToRatios(ratios);
		int[] indexExpected = {34, 55, 100};
		for(int i = 0; i < 3; i++){
			assertTrue(indexObtained[i] == indexExpected[i]);
		}
	}
	
	@Test
	public void splitDataTest(){
		createBiggerDataSet(42);
		double[] ratios = {0.17, 0.43, 0.40};
		int[] expectedSize = {7, 18, 17};
		List<JavaRDD<Rating>> splittedDataSet = dataSet.splitUserData(ratios);
		for(int i = 0; i < 3; i++){
			assertTrue(splittedDataSet.get(i).count()==expectedSize[i]);
		}
		assertTrue(splittedDataSet.get(0).union(splittedDataSet.get(1)).union(splittedDataSet.get(2)).count() == 42);
		
	}
	
	private List<Integer> getListFromNToM(int n, int m){
		List<Integer> list = new ArrayList<Integer>(m-n+1);
		for(int i = n; i <= m; i++){
			list.add(i);
		}
		return list;
	}
	
	private void createBiggerDataSet(int dataSetSize){
		dataSet.close();
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		for(int i = 0; i < dataSetSize; i++){
			events.add(new RecsysTVEvent((short)1,(short)2,(byte)3,(byte)4,(byte)81,1 + i,202344 + i,50880093 + i,5));
		}
		JavaSparkContext defaultJavaSparkContext = SparkUtilities.getADefaultSparkContext();
		JavaRDD<RecsysTVEvent> eventsRDD = SparkUtilities.<RecsysTVEvent>elementsToJavaRDD(events, defaultJavaSparkContext);
		dataSet = new RecsysTVDataSet(eventsRDD, defaultJavaSparkContext);
	}
	
	@After
	public void tearDown(){
		dataSet.close();
	}
}
