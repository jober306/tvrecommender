package model.tensor;

import static model.tensor.UserPreferenceTensorCollection.ANY;
import static model.tensor.UserPreferenceTensorCollection.getAnyFeatureVector;
import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.RecsysTVDataSet;
import data.recsys.feature.RecsysFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.tensor.RecsysUserPreferenceTensorCalculator;
import util.spark.SparkUtilities;

public class UserPrefenreceTensorCalculatorTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static RecsysTVDataSetLoader loader;
	static RecsysTVDataSet dataSet;
	
	@BeforeClass
	public static void setUp(){
		sc = SparkUtilities.getADefaultSparkContext();
		loader = new RecsysTVDataSetLoader(path, sc);
		dataSet = loader.loadDataSet()._2();
	}
	
	@Test
	public void calculateUserPreferenceTensorWithAnyProgramAndSlot(){
		RecsysUserPreferenceTensorCalculator calculator = new RecsysUserPreferenceTensorCalculator();
		UserPreferenceTensorCollection tensors = calculator.calculateUserPreferenceTensorForDataSet(dataSet, RecsysFeatureExtractor.getInstance(), false, true, true);
		UserPreference user1Pref = new UserPreference(1, getAnyFeatureVector(4), (short) ANY);
		UserPreferenceTensor user1Tensors = tensors.getUserPreferenceTensor(user1Pref);
		int expectedWatchTime = 61;
		int actualTotalWatchTime = user1Tensors.totalWatchTime();
		assertEquals(expectedWatchTime, actualTotalWatchTime);
	}
	
	@Test 
	public void calculateUserPreferenceTensorWithAnySlot(){
		RecsysUserPreferenceTensorCalculator calculator = new RecsysUserPreferenceTensorCalculator();
		UserPreferenceTensorCollection tensors = calculator.calculateUserPreferenceTensorForDataSet(dataSet, RecsysFeatureExtractor.getInstance(), false, false, true);
		int expectedTotalWatchTimeUser2 = 6;
		UserPreference user2Pref = new UserPreference(2, Vectors.dense(new double[]{46,19,5,81}), (short) ANY);
		int actualTotalWatchTimeUser2 = tensors.getUserPreferenceTensor(user2Pref).totalWatchTime();
		assertEquals(expectedTotalWatchTimeUser2, actualTotalWatchTimeUser2);
	}
	
	@AfterClass
	public static void tearDown(){
		dataSet.close();
		sc.close();
		loader = null;
	}
}
