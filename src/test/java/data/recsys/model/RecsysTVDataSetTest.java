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
	final RecsysTVEvent tvEvent2 = new RecsysTVEvent((short)4,(short)7,(byte)1,(byte)6,(byte)11,3,5785,51122125,6);
	final RecsysTVEvent tvEvent3 = new RecsysTVEvent((short)6,(short)33,(byte)1,(byte)4,(byte)30,3,5785,51097405,5);
	
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
		assertTrue(userIds.contains(1));
		assertTrue(userIds.contains(3));
	}
	
	@Test
	public void getAllProgramIdsTest(){
		List<Integer> programIds = dataSet.getAllProgramIds();
		assertTrue(programIds.contains(202344));
		assertTrue(programIds.contains(5785));
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
	public void getIndexesCorrespondingToRatiosTest(){
		
	}
	
	@After
	public void tearDown(){
		dataSet.close();
	}
}
