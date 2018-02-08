package model.tensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import model.tensor.UserPreferenceTensor;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTensorTest {
	
	static final int userId = 100;
	static final double[] programFeatureValues = new double[]{1,2,3,4};
	static final Vector programFeature = Vectors.dense(programFeatureValues);
	static final short slot = 2;
	static final UserPreference userPref = new UserPreference(userId, programFeature, slot);
	
	UserPreferenceTensor tensor;
	
	@Before
	public void setUp(){
		tensor = new UserPreferenceTensor(userPref);
	}
	
	@Test
	public void getUserIdTest(){
		assertEquals(userId, tensor.userId());
	}
	
	@Test
	public void getProgramFeatureTest(){
		assertArrayEquals(programFeatureValues, tensor.programFeatureVector().toArray(), 0.0d);
	}
	
	@Test
	public void getSlotTest(){
		assertEquals(slot, tensor.slot());
	}
	
	@Test
	public void getTotalWatchTime(){
		assertEquals(0, tensor.totalWatchTime());
	}
	
	@Test
	public void incrementValueTest(){
		int watchTime = 10;
		tensor.incrementValue(watchTime);
		assertEquals(watchTime, tensor.totalWatchTime());
	}
	
	@Test
	public void equalsTest(){
		UserPreferenceTensor copyTensor = new UserPreferenceTensor(new UserPreference(tensor.userId(), tensor.programFeatureVector(), tensor.slot()));
		assertTrue(copyTensor.equals(tensor));
		assertTrue(tensor.equals(copyTensor));
	}
	
	@Test
	public void hashTest(){
		UserPreferenceTensor copyTensor = new UserPreferenceTensor(userPref);
		assertEquals(tensor.hashCode(), copyTensor.hashCode());
	}
	
	@After
	public void tearDown(){
		tensor = null;
	}
}
