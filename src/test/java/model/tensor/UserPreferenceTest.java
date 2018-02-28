package model.tensor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTest {
	
	static final int userId = 100;
	static final double[] programFeatureValues = new double[]{1,2,3,4};
	static final Vector programFeature = Vectors.dense(programFeatureValues);
	static final short slot = 2;
	
	
	UserPreference userPref;
	
	@Before
	public void setUp(){
		userPref = new UserPreference(userId, programFeature, slot);
	}
	
	@Test
	public void getUserIdTest(){
		assertEquals(userId, userPref.userId());
	}
	
	@Test
	public void getProgramFeatureTest(){
		assertArrayEquals(programFeatureValues, userPref.programFeatureVector().toArray(), 0.0d);
	}
	
	@Test
	public void getSlotTest(){
		assertEquals(slot, userPref.slot());
	}
	
	@Test
	public void equalsTest(){
		UserPreference copyTensor = new UserPreference(userPref.userId(), userPref.programFeatureVector(), userPref.slot());
		assertTrue(copyTensor.equals(userPref));
		assertTrue(userPref.equals(copyTensor));
	}
	
	@Test
	public void hashTest(){
		UserPreferenceTensor copyTensor = new UserPreferenceTensor(userPref);
		assertEquals(userPref.hashCode(), copyTensor.hashCode());
	}
	
	@After
	public void tearDown(){
		userPref = null;
	}
}
