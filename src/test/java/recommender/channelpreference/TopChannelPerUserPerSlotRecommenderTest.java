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

public class TopChannelPerUserPerSlotRecommenderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static ChannelPreferenceRecommender recommender;

	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet();
		Context<User, RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1(), data._2());
		recommender = new TopChannelPerUserPerSlotRecommender(context, 10);
		recommender.train();
	}
	
	@Test
	public void testRecommendMostWatchedProgramButNotInGoodSlotUser1(){
		User user = new User(1);
		int expectedProgramId = 10;
		RecsysTVProgram mostWatchedChannelTVProgramNotInSlot = new RecsysTVProgram((short)0, (short)0, (short)46, expectedProgramId, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram1 = new RecsysTVProgram((short)0, (short)0, (short)1, 1, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram2 = new RecsysTVProgram((short)0, (short)0, (short)2, 2, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelTVProgramNotInSlot, notWatchedChannelTVProgram1, notWatchedChannelTVProgram2);
		//Programs that are on a channel that the user has never watched are not recommended at all.
		int expectedNumberOfResults = 0;
		recommender.setNumberOfRecommendations(expectedNumberOfResults);
		Recommendations<User, RecsysTVProgram> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
	}
	
	@Test
	public void testRecommendMostWatchedProgramUser1(){
		User user = new User(1);
		int expectedProgramId = 10;
		RecsysTVProgram mostWatchedChannelTVProgram = new RecsysTVProgram((short)0, (short)19, (short)46, expectedProgramId, (byte) 0, (byte) 0);
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
	public void testRecommendMosWatchedProgramPerSlotUser1(){
		User user = new User(1);
		RecsysTVProgram mostWatchedChannelAndSlotTVProgram = new RecsysTVProgram((short)0, (short)20, (short)46, 2345, (byte) 0, (byte) 0);
		RecsysTVProgram secondMostWatchedChannelAndSlotTVProgram = new RecsysTVProgram((short)0, (short)19, (short)46, 1234, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelAndSlotTVProgram, secondMostWatchedChannelAndSlotTVProgram);
		//Programs that are on a channel that the user has never watched are not recommended at all.
		int expectedNumberOfResults = 2;
		recommender.setNumberOfRecommendations(expectedNumberOfResults);
		Recommendations<User, RecsysTVProgram> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
		assertEquals(mostWatchedChannelAndSlotTVProgram.id(), recommendations.get(0).id());
		assertEquals(secondMostWatchedChannelAndSlotTVProgram.id(), recommendations.get(1).id());
	}
	
	@AfterClass
	public static void tearDownOnce() {
		sc.close();
	}
}
