package mllib.recommender;

import static org.junit.Assert.assertEquals;
import static data.recsys.model.RecsysTVDataSet.START_TIME;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.model.recsys.model.tensor.RecsysUserPreferenceTensorCalculator;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysEPG;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import data.recsys.model.RecsysTVProgram;
import scala.Tuple2;

public class TopChannelRecommenderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static TopChannelRecommender<RecsysTVProgram, RecsysTVEvent> predictor;
	
	@BeforeClass
	public static void setUpOnce(){
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
		predictor = new TopChannelRecommender<RecsysTVProgram, RecsysTVEvent>(data._1(), data._2(), new RecsysUserPreferenceTensorCalculator());
	}
	
	@Test
	public void recommendTest(){
		int expectedRecommendation = 202344;
		int recommendation = predictor.recommend(0, START_TIME.plusHours(19), 1).get(0);
		assertEquals(expectedRecommendation, recommendation);
	}
	
	@AfterClass
	public static void tearDownOnce(){
		data._2().close();
	}
}
