package model.tensor;

import static org.junit.Assert.*;

import java.util.List;

import static model.tensor.UserPreferenceTensorCollection.ANY;
import static model.tensor.UserPreferenceTensorCollection.getAnyFeatureVector;
import model.tensor.UserPreferenceTensor;
import model.tensor.UserPreferenceTensorCollection;

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
	
	UserPreferenceTensorCollection collection;
	
	@Before
	public void setUpOnce(){
		collection = new UserPreferenceTensorCollection();
		collection.add(tensor1);
		collection.add(tensor2);
		collection.add(tensor3);
		collection.add(tensor4);
		collection.add(tensor5);
		collection.add(tensor6);
		collection.add(tensor7);
	}
	
	@Test
	public void emptyTest(){
		assertTrue(!collection.isEmpty());
		UserPreferenceTensorCollection emptyCollection = new UserPreferenceTensorCollection();
		assertTrue(emptyCollection.isEmpty());
	}
	
	@Test
	public void getAllTest(){
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		assertEquals(7, tensors.size());
	}
	
	@Test
	public void allWatchTimeZeros(){
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		for(UserPreferenceTensor tensor : tensors){
			assertEquals(0, tensor.totalWatchTime());
		}
	}
	
	@Test
	public void getUser1Test(){
		List<UserPreferenceTensor> user1tensors = collection.getUserPreferenceTensors(new UserPreference(1, getAnyFeatureVector(2), ANY));
		assertEquals(2, user1tensors.size());
		assertTrue(user1tensors.contains(tensor1));
		assertTrue(user1tensors.contains(tensor5));
	}
	
	@Test
	public void getUser1Slot1Test(){
		List<UserPreferenceTensor> user1slot1tensor = collection.getUserPreferenceTensors(new UserPreference(1, getAnyFeatureVector(2), 1));
		assertEquals(1, user1slot1tensor.size());
		assertTrue(user1slot1tensor.contains(tensor5));
	}
	
	@Test
	public void getFeature44Test(){
		double[] features44 = new double[]{4,4};
		List<UserPreferenceTensor> feature44Tensor = collection.getUserPreferenceTensors(new UserPreference(ANY, Vectors.dense(features44), ANY));
		assertEquals(1, feature44Tensor.size());
		assertTrue(feature44Tensor.contains(tensor7));
	}
	
	@Test
	public void getAllWithAnyTest(){
		List<UserPreferenceTensor> tensors = collection.getUserPreferenceTensors(new UserPreference(ANY, getAnyFeatureVector(2), ANY));
		assertEquals(7, tensors.size());
	}
	
	@Test
	public void addTensorWatchTimeIncreasedTest(){
		int watchTime = 10;
		tensorToAdd.incrementValue(watchTime);
		collection.add(tensorToAdd);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		assertEquals(7, tensors.size());
		UserPreferenceTensor addedTensor = collection.getUserPreferenceTensors(new UserPreference(1, Vectors.dense(new double[]{0,1}), (short)5)).get(0);
		assertEquals(watchTime, addedTensor.totalWatchTime());
	}
	
	@Test
	public void addNewTensorTest(){
		UserPreferenceTensor newTensor = new UserPreferenceTensor(new UserPreference(12, Vectors.dense(new double[]{0,0}), (short)10));
		collection.add(newTensor);
		List<UserPreferenceTensor> tensors = collection.getAllUserPreferenceTensors();
		assertEquals(8, tensors.size());
		assertTrue(tensors.contains(newTensor));
	}
	
	@After
	public void tearDown(){
		collection = null;
	}
}
