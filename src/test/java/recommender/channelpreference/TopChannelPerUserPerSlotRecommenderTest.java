package recommender.channelpreference;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.Context;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import scala.Tuple2;

public class TopChannelPerUserPerSlotRecommenderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static ChannelPreferenceRecommender recommender;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
		Context<RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1(), data._2());
		recommender = new TopChannelPerUserPerSlotRecommender(context, 10);
		recommender.train();
	}
	
	@Test
	public void testRecommendMostWatchedProgramButNotInGoodSlotUser1(){
		int user = 1;
		int expectedProgramId = 10;
		RecsysTVProgram mostWatchedChannelTVProgramNotInSlot = new RecsysTVProgram((short)0, (short)0, 46, expectedProgramId, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram1 = new RecsysTVProgram((short)0, (short)0, 1, 1, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram2 = new RecsysTVProgram((short)0, (short)0, 2, 2, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelTVProgramNotInSlot, notWatchedChannelTVProgram1, notWatchedChannelTVProgram2);
		//Programs that are on a channel that the user has never watched are not recommended at all.
		int expectedNumberOfResults = 0;
		recommender.setNumberOfRecommendations(expectedNumberOfResults);
		Recommendations<Recommendation> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
	}
	
	@Test
	public void testRecommendMostWatchedProgramUser1(){
		int user = 1;
		int expectedProgramId = 10;
		RecsysTVProgram mostWatchedChannelTVProgram = new RecsysTVProgram((short)0, (short)19, 46, expectedProgramId, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram1 = new RecsysTVProgram((short)0, (short)0, 1, 1, (byte) 0, (byte) 0);
		RecsysTVProgram notWatchedChannelTVProgram2 = new RecsysTVProgram((short)0, (short)0, 2, 2, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelTVProgram, notWatchedChannelTVProgram1, notWatchedChannelTVProgram2);
		//Programs that are on a channel that the user has never watched are not recommended at all.
		int expectedNumberOfResults = 1;
		recommender.setNumberOfRecommendations(expectedNumberOfResults);
		Recommendations<Recommendation> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
		assertEquals(expectedProgramId, recommendations.get(0).tvProgram().programId());
	}
	
	@Test
	public void testRecommendMosWatchedProgramPerSlotUser1(){
		int user = 1;
		RecsysTVProgram mostWatchedChannelAndSlotTVProgram = new RecsysTVProgram((short)0, (short)20, 46, 2345, (byte) 0, (byte) 0);
		RecsysTVProgram secondMostWatchedChannelAndSlotTVProgram = new RecsysTVProgram((short)0, (short)19, 46, 1234, (byte) 0, (byte) 0);
		List<RecsysTVProgram> tvPrograms = Arrays.asList(mostWatchedChannelAndSlotTVProgram, secondMostWatchedChannelAndSlotTVProgram);
		//Programs that are on a channel that the user has never watched are not recommended at all.
		int expectedNumberOfResults = 2;
		recommender.setNumberOfRecommendations(expectedNumberOfResults);
		Recommendations<Recommendation> recommendations = recommender.recommend(user, tvPrograms);
		assertEquals(expectedNumberOfResults, recommendations.size());
		assertEquals(mostWatchedChannelAndSlotTVProgram.programId(), recommendations.get(0).tvProgram().programId());
		assertEquals(secondMostWatchedChannelAndSlotTVProgram.programId(), recommendations.get(1).tvProgram().programId());
	}
	
	@AfterClass
	public static void tearDownOnce() {
		data._2().close();
	}
}
