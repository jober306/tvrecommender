package mllib.recommender;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.model.recsys.model.tensor.RecsysUserPreferenceTensorCalculator;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import data.recsys.model.RecsysTVProgram;

public class TopChannelRecommenderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static RecsysTVDataSet dataSet;
	static TopChannelRecommender<RecsysTVProgram, RecsysTVEvent> predictor;
	
	@BeforeClass
	public static void setUpOnce(){
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		dataSet = loader.loadDataSet();
		predictor = new TopChannelRecommender<RecsysTVProgram, RecsysTVEvent>(dataSet, new RecsysUserPreferenceTensorCalculator());
	}
	
	@Test
	public void recommendTest(){
		int expectedRecommendation = 202344;
		int recommendation = predictor.recommend(1, 19);
		assertEquals(expectedRecommendation, recommendation);
	}
	
	@AfterClass
	public static void tearDownOnce(){
		dataSet.close();
	}
}
