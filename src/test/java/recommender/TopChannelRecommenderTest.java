package recommender;

import static data.recsys.RecsysTVDataSet.START_TIME;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.TopChannelRecommender;
import scala.Tuple2;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.tensor.RecsysUserPreferenceTensorCalculator;

public class TopChannelRecommenderTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static TopChannelRecommender<RecsysTVProgram, RecsysTVEvent> predictor;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
		predictor = new TopChannelRecommender<RecsysTVProgram, RecsysTVEvent>(
				data._1(), data._2(),
				new RecsysUserPreferenceTensorCalculator());
	}

	@Test
	public void recommendTest() {
		int expectedRecommendation = 254329;
		int recommendation = predictor
				.recommend(0, START_TIME.plusHours(19), 1).get(0);
		assertEquals(expectedRecommendation, recommendation);
	}

	@AfterClass
	public static void tearDownOnce() {
		data._2().close();
	}
}
