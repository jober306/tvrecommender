package mllib.model.tensor;

import static org.junit.Assert.assertEquals;

import java.util.List;

import static mllib.model.tensor.UserPreferenceTensorCollection.ANY;
import static mllib.model.tensor.UserPreferenceTensorCollection.getAnyFeatureVector;

import org.apache.spark.mllib.linalg.Vectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.model.recsys.model.tensor.RecsysUserPreferenceTensorCalculator;
import data.recsys.feature.RecsysFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;

public class UserPrefenreceTensorCalculatorTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static RecsysTVDataSetLoader loader;
	static RecsysTVDataSet dataSet;
	
	@BeforeClass
	public static void setUp(){
		loader = new RecsysTVDataSetLoader(path);
		dataSet = loader.loadDataSet();
	}
	
	@Test
	public void calculateUserPreferenceTensorForDataSetTest(){
		RecsysUserPreferenceTensorCalculator calculator = new RecsysUserPreferenceTensorCalculator();
		UserPreferenceTensorCollection tensors = calculator.calculateUserPreferenceTensorForDataSet(dataSet, RecsysFeatureExtractor.getInstance());
		List<UserPreferenceTensor> user1Tensors = tensors.getUserPreferenceTensors(1, getAnyFeatureVector(4), ANY);
		assertEquals(3, user1Tensors.size());
		int expectedWatchTime = 61;
		int actualTotalWatchTime = user1Tensors.stream().mapToInt(tensor -> tensor.getTotalWatchTime()).sum();
		assertEquals(expectedWatchTime, actualTotalWatchTime);
		int expectedTotalWatchTimeUser2 = 6;
		int actualTotalWatchTimeUser2 = tensors.getUserPreferenceTensors(2, Vectors.dense(new double[]{46,19,5,81}), 19).get(0).getTotalWatchTime();
		assertEquals(expectedTotalWatchTimeUser2, actualTotalWatchTimeUser2);
	}
	
	@AfterClass
	public static void tearDown(){
		dataSet.close();
		loader = null;
	}
}
