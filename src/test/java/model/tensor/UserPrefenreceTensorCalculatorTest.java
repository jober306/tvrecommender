package model.tensor;

import static org.junit.Assert.assertEquals;

import java.util.List;

import static model.tensor.UserPreferenceTensorCollection.ANY;
import static model.tensor.UserPreferenceTensorCollection.getAnyFeatureVector;
import model.tensor.UserPreferenceTensor;
import model.tensor.UserPreferenceTensorCollection;

import org.apache.spark.mllib.linalg.Vectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.RecsysTVDataSet;
import data.recsys.feature.RecsysFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.tensor.RecsysUserPreferenceTensorCalculator;

public class UserPrefenreceTensorCalculatorTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static RecsysTVDataSetLoader loader;
	static RecsysTVDataSet dataSet;
	
	@BeforeClass
	public static void setUp(){
		loader = new RecsysTVDataSetLoader(path);
		dataSet = loader.loadDataSet()._2();
	}
	
	@Test
	public void calculateUserPreferenceTensorForDataSetTest(){
		RecsysUserPreferenceTensorCalculator calculator = new RecsysUserPreferenceTensorCalculator();
		UserPreferenceTensorCollection tensors = calculator.calculateUserPreferenceTensorForDataSet(dataSet, RecsysFeatureExtractor.getInstance());
		UserPreference user1Pref = new UserPreference(1, getAnyFeatureVector(4), ANY);
		List<UserPreferenceTensor> user1Tensors = tensors.getUserPreferenceTensors(user1Pref);
		assertEquals(3, user1Tensors.size());
		int expectedWatchTime = 61;
		int actualTotalWatchTime = user1Tensors.stream().mapToInt(tensor -> tensor.getTotalWatchTime()).sum();
		assertEquals(expectedWatchTime, actualTotalWatchTime);
		int expectedTotalWatchTimeUser2 = 6;
		UserPreference user2Pref = new UserPreference(2, Vectors.dense(new double[]{46,19,5,81}), 19);
		int actualTotalWatchTimeUser2 = tensors.getUserPreferenceTensors(user2Pref).get(0).getTotalWatchTime();
		assertEquals(expectedTotalWatchTimeUser2, actualTotalWatchTimeUser2);
	}
	
	@AfterClass
	public static void tearDown(){
		dataSet.close();
		loader = null;
	}
}
