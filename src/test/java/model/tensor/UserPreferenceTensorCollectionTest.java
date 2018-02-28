package model.tensor;

import static model.tensor.UserPreferenceTensorCollection.ANY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTensorCollectionTest {
	
	UserPreferenceTensor tensor1 = new UserPreferenceTensor(new UserPreference(1, Vectors.dense(new double[]{0,1}), (short)5));
	UserPreferenceTensor tensor2 = new UserPreferenceTensor(new UserPreference(2, Vectors.dense(new double[]{1,2}), (short)4));
	UserPreferenceTensor tensor3 = new UserPreferenceTensor(new UserPreference(3, Vectors.dense(new double[]{2,3}), (short)3));
	UserPreferenceTensor tensor4 = new UserPreferenceTensor(new UserPreference(4, Vectors.dense(new double[]{3,2}), (short)2));
	UserPreferenceTensor tensor5 = new UserPreferenceTensor(new UserPreference(1, Vectors.dense(new double[]{2,1}), (short)1));
	UserPreferenceTensor tensor6 = new UserPreferenceTensor(new UserPreference(2, Vectors.dense(new double[]{1,0}), (short)2));
	UserPreferenceTensor tensor7 = new UserPreferenceTensor(new UserPreference(3, Vectors.dense(new double[]{4,4}), (short)3));
	UserPreferenceTensor tensorToAdd = new UserPreferenceTensor(new UserPreference(1, Vectors.dense(new double[]{0,1}), (short)5));
	
	List<UserPreferenceTensor> tensors;
	
	@Before
	public void setUp(){
		tensors = Arrays.asList(tensor1, tensor2, tensor3, tensor4, tensor5, tensor6, tensor7);
	}
	
	@Test
	public void emptyTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, false, 2, false);
		tensors.stream().forEach(collection::add);
		assertTrue(!collection.isEmpty());
		UserPreferenceTensorCollection emptyCollection = new UserPreferenceTensorCollection(false, false, 0, false);
		assertTrue(emptyCollection.isEmpty());
	}
	
	@Test
	public void getAllTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, false, 2, false);
		tensors.stream().forEach(collection::add);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		assertEquals(7, tensors.size());
	}
	
	@Test
	public void allWatchTimeZeros(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, false, 2, false);
		tensors.stream().forEach(collection::add);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		for(UserPreferenceTensor tensor : tensors){
			assertEquals(0, tensor.totalWatchTime());
		}
	}
	
	@Test
	public void getAnyProgramTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, true, 2, false);
		tensors.stream().forEach(collection::add);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		int expectedSize = 6;
		int actualSize = tensors.size();
		assertEquals(expectedSize, actualSize);
	}
	
	@Test
	public void getAnyProgramAnySlotTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, true, 2, false);
		tensors.stream().forEach(collection::add);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		int expectedSize = 6;
		int actualSize = tensors.size();
		assertEquals(expectedSize, actualSize);
	}
	
	@Test
	public void getAnyUserAnySlotTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(true, false, 2, true);
		tensors.stream().forEach(collection::add);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		int expectedSize = 7;
		int actualSize = tensors.size();
		assertEquals(expectedSize, actualSize);
	}
	
	@Test
	public void getProramFeature44ExistsTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(true, false, 2, true);
		tensors.stream().forEach(collection::add);
		Vector program44 = Vectors.dense(new double[]{4.0d,4.0d});
		UserPreferenceTensor tensor = collection.getUserPreferenceTensor(new UserPreference(ANY, program44, (short) ANY));
		assertNotNull(tensor);
	}
	
	@Test
	public void getAllNoAnyTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, false, 2, false);
		tensors.stream().forEach(collection::add);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		int expectedSize = 7;
		int actualSize = tensors.size();
		assertEquals(expectedSize, actualSize);
	}
	
	@Test
	public void addTensorWatchTimeIncreasedTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, false, 2, false);
		tensors.stream().forEach(collection::add);
		int watchTime = 10;
		tensorToAdd.incrementValue(watchTime);
		collection.add(tensorToAdd);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		assertEquals(7, tensors.size());
		UserPreferenceTensor addedTensor = collection.getUserPreferenceTensor(new UserPreference(1, Vectors.dense(new double[]{0,1}), (short)5));
		assertEquals(watchTime, addedTensor.totalWatchTime());
	}
	
	@Test
	public void addNewTensorTest(){
		UserPreferenceTensorCollection collection = new UserPreferenceTensorCollection(false, false, 2, false);
		tensors.stream().forEach(collection::add);
		UserPreferenceTensor newTensor = new UserPreferenceTensor(new UserPreference(12, Vectors.dense(new double[]{0,0}), (short)10));
		collection.add(newTensor);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		assertEquals(8, tensors.size());
		assertTrue(tensors.contains(newTensor));
	}
	
	@After
	public void tearDown(){
		tensors = null;
	}
}
