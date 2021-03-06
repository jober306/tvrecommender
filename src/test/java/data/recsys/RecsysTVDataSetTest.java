package data.recsys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.feature.RecsysFeatureExtractor;
import data.recsys.utility.RecsysUtilities;
import model.data.User;
import model.data.mapping.IdentityMapping;
import model.matrix.DistributedUserTVProgramMatrix;
import model.matrix.LocalUserTVProgramMatrix;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class RecsysTVDataSetTest {
	
	static JavaSparkContext sc;
	
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
	IdentityMapping<User> userMapping;
	IdentityMapping<RecsysTVProgram> tvProgramMapping;
	
	@BeforeClass
	public static void setUpOnce(){
		sc = SparkUtilities.getADefaultSparkContext();
	}
	
	@Before
	public void setUp() {
		List<RecsysTVEvent> events = new ArrayList<RecsysTVEvent>();
		events.add(tvEvent1);
		events.add(tvEvent2);
		events.add(tvEvent3);
		events.add(tvEvent4);
		JavaRDD<RecsysTVEvent> eventsRDD = SparkUtilities.elementsToJavaRDD(events,sc);
		dataSet = new RecsysTVDataSet(eventsRDD);
		userMapping = new IdentityMapping<>(dataSet.allUsers());
		tvProgramMapping = new IdentityMapping<>(dataSet.allPrograms());
	}
	
	@Test
	public void startTimeConvertedProperlyTest(){
		LocalDateTime expectedTime = RecsysUtilities.START_TIME.plusWeeks(2).plusHours(1);
		LocalDateTime actualTime = tvEvent1.watchTime();
		assertEquals(expectedTime, actualTime);
	}

	@Test
	public void convertDataSetToMLlibRatingsTest() {
		JavaRDD<Rating> ratings = dataSet.convertToMLlibRatings();
		assertTrue(ratings.count() == 4);
		final Set<Integer> expectedUserIds = dataSet.allUserIds();
		final Set<Integer> expectedProgramIds = dataSet.allProgramIds();
		ratings.foreach(rating -> {
			assertTrue(expectedUserIds.contains(rating.user()));
			assertTrue(expectedProgramIds.contains(rating.product()));
			assertTrue(rating.rating() == 1);
		});
	}

	@Test
	public void convertToDistributedMatrixTest() {
		IdentityMapping<User> userMapping = new IdentityMapping<>(dataSet.allUsers());
		IdentityMapping<RecsysTVProgram> tvProgramMapping = new IdentityMapping<>(dataSet.allPrograms());
		DistributedUserTVProgramMatrix<User, User, RecsysTVProgram, RecsysTVProgram> R = dataSet.computeDistUserItemMatrix(userMapping, tvProgramMapping);
		Set<Tuple2<Integer, Integer>> seenIndexes = dataSet.events().collect().stream().map(event -> new Tuple2<>(userMapping.valueToIndex(event.user()), tvProgramMapping.valueToIndex(event.program()))).collect(Collectors.toSet());
		for (int row = 0; row < R.getNumRows(); row++) {
			for(int col = 0; col < R.getNumCols(); col++){
				double actualValue = R.getValue(row, col);
				Tuple2<Integer, Integer> currentIndexes = new Tuple2<>(row, col);
				if(seenIndexes.contains(currentIndexes)){
					double expectedValue = 1.0d;
					assertEquals(expectedValue, actualValue, 0.0d);
				}else{
					double expectedValue = 0.0d;
					assertEquals(expectedValue, actualValue, 0.0d);
				}
			}
		}
	}
	
	@Test
	public void convertToLocalMatrixTest(){
		IdentityMapping<User> userMapping = new IdentityMapping<>(dataSet.allUsers());
		IdentityMapping<RecsysTVProgram> tvProgramMapping = new IdentityMapping<>(dataSet.allPrograms());
		LocalUserTVProgramMatrix<User, User, RecsysTVProgram, RecsysTVProgram> R = dataSet.computeLocalUserItemMatrix(userMapping, tvProgramMapping);
		Set<Tuple2<Integer, Integer>> seenIndexes = dataSet.events().collect().stream().map(event -> new Tuple2<>(userMapping.valueToIndex(event.user()), tvProgramMapping.valueToIndex(event.program()))).collect(Collectors.toSet());
		for (int row = 0; row < R.getNumRows(); row++) {
			for(int col = 0; col < R.getNumCols(); col++){
				double actualValue = R.getValue(row, col);
				Tuple2<Integer, Integer> currentIndexes = new Tuple2<>(row, col);
				if(seenIndexes.contains(currentIndexes)){
					double expectedValue = 1.0d;
					assertEquals(expectedValue, actualValue, 0.0d);
				}else{
					double expectedValue = 0.0d;
					assertEquals(expectedValue, actualValue, 0.0d);
				}
			}
		}
	}

	@Test
	public void getContentMatrixTest() {
		IndexedRowMatrix C = dataSet.computeContentMatrix(RecsysFeatureExtractor.getInstance(), tvProgramMapping);
		assertEquals(3, C.rows().count());
		assertEquals(3, C.numRows());
		assertEquals(4, C.numCols());
	}
	
	@AfterClass
	public static void tearDownOnce(){
		sc.close();
	}
}
