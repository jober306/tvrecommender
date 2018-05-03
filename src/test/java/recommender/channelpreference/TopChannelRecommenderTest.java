package recommender.channelpreference;

import static data.recsys.RecsysTVDataSet.START_TIME;
import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.Context;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import model.data.User;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class TopChannelRecommenderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static ChannelPreferenceRecommender recommender;

	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet();
		Context<User, RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1, data._2);
		recommender = new TopChannelRecommender(context, 10);
		recommender.train();
	}
	
	@Test
	public void recommendTest() {
		int expectedRecommendation = 254329;
		int recommendation = recommender.recommend(new User(0), START_TIME.plusHours(19)).get(0).id();
		assertEquals(expectedRecommendation, recommendation);
	}
	
	@AfterClass
	public static void tearDownOnce() {
		sc.close();
	}
}
