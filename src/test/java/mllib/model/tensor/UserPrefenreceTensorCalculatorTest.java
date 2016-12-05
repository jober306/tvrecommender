package mllib.model.tensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import static mllib.model.tensor.UserPreferenceTensorCollection.ANY;
import static mllib.model.tensor.UserPreferenceTensorCollection.getAnyFeatureVector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;

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
		UserPreferenceTensorCalculator<RecsysTVEvent> calculator = new UserPreferenceTensorCalculator<RecsysTVEvent>();
		UserPreferenceTensorCollection tensors = calculator.calculateUserPreferenceTensorForDataSet(dataSet);
		List<UserPreferenceTensor> user1Tensors = tensors.getUserPreferenceTensors(1, getAnyFeatureVector(4), ANY);
		assertEquals(3, user1Tensors.size());
	}
	
	@AfterClass
	public static void tearDown(){
		dataSet.close();
		loader = null;
	}
}
