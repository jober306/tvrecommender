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
	
	UserPreferenceTensor tensor;
	
	@Before
	public void setUp(){
		tensor = new UserPreferenceTensor(userId, programFeature, slot);
	}
	
	@Test
	public void getUserIdTest(){
		assertEquals(userId, tensor.getUserId());
	}
	
	@Test
	public void getProgramFeatureTest(){
		assertArrayEquals(programFeatureValues, tensor.getProgramFeatureVector().toArray(), 0.0d);
	}
	
	@Test
	public void getSlotTest(){
		assertEquals(slot, tensor.getSlot());
	}
	
	@Test
	public void getTotalWatchTime(){
		assertEquals(0, tensor.getTotalWatchTime());
	}
	
	@Test
	public void incrementValueTest(){
		int watchTime = 10;
		tensor.incrementValue(watchTime);
		assertEquals(watchTime, tensor.getTotalWatchTime());
	}
	
	@Test
	public void equalsTest(){
		UserPreferenceTensor copyTensor = new UserPreferenceTensor(tensor.getUserId(), tensor.getProgramFeatureVector(), tensor.getSlot());
		assertTrue(copyTensor.equals(tensor));
		assertTrue(tensor.equals(copyTensor));
	}
	
	@Test
	public void hashTest(){
		UserPreferenceTensor copyTensor = new UserPreferenceTensor(tensor.getUserId(), tensor.getProgramFeatureVector(), tensor.getSlot());
		assertEquals(tensor.hashCode(), copyTensor.hashCode());
	}
	
	@After
	public void tearDown(){
		tensor = null;
	}
}
