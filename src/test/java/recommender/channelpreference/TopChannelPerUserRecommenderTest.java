package recommender.channelpreference;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
import model.recommendation.Recommendations;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class TopChannelPerUserRecommenderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static ChannelPreferenceRecommender recommender;

	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet();
		Context<User, RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1, data._2);
		recommender = new TopChannelPerUserRecommender(context, 10);
		recommender.train();
	}
	
	@Test
	public void testRecommendMostWatchedProgramUser1(){
		User user = new User(1);
		int expectedProgramId = 10;
		RecsysTVProgram mostWatchedChannelTVProgram = new RecsysTVProgram((short)0, (short)0, (short)46, expectedProgramId, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram1 = new RecsysTVProgram((short)0, (short)0, (short)1, 1, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram2 = new RecsysTVProgram((short)0, (short)0, (short)2, 2, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelTVProgram, notWatchedChannelTVProgram1, notWatchedChannelTVProgram2);
		//Programs that are on a channel that the user has never watched are not recommended at all.
		int expectedNumberOfResults = 1;
		recommender.setNumberOfRecommendations(expectedNumberOfResults);
		Recommendations<User, RecsysTVProgram> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
		assertEquals(expectedProgramId, recommendations.get(0).id());
	}
	
	@Test
	public void testRecommendMostWatchProgramUser2(){
		User user = new User(2);
		int expectedFirstProgramId1 = 10;
		int expectedSecondProgramId2 = 15;
		int expectedThirdProgramId3 = 20;
		RecsysTVProgram mostWatchedChannelTVProgram1 = new RecsysTVProgram((short)0, (short)0, (short)46, expectedFirstProgramId1, (byte) 0, (byte) 0);
		RecsysTVProgram mostWatchedChannelTVProgram2 = new RecsysTVProgram((short)0, (short)0, (short)1, expectedSecondProgramId2, (byte) 0, (byte) 0);
		RecsysTVProgram mostWatchedChannelTVProgram3 = new RecsysTVProgram((short)0, (short)0, (short)4, expectedThirdProgramId3, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram = new RecsysTVProgram((short)0, (short)0, (short)2, 2, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelTVProgram1, mostWatchedChannelTVProgram2, mostWatchedChannelTVProgram3, notWatchedChannelTVProgram);
		int expectedNumberOfResults = 4;
		recommender.setNumberOfRecommendations(tvPrograms.size());
		Recommendations<User, RecsysTVProgram> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
		assertEquals(expectedFirstProgramId1, recommendations.get(0).id());
		assertEquals(expectedSecondProgramId2, recommendations.get(1).id());
		assertEquals(expectedThirdProgramId3, recommendations.get(2).id());
	}
	
	@AfterClass
	public static void tearDownOnce() {
		sc.close();
	}
}
