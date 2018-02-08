package recommender;

import static data.recsys.RecsysTVDataSet.START_TIME;
import static org.junit.Assert.assertEquals;

import java.util.List;

import model.Recommendation;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.Tuple2;
import data.Context;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;

public class SpaceAlignmentPredictorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final static int r = 100;
	final static int numberOfResults = 2;
	final static int neighbourhoodSize = 2;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static RecsysEPG epg;
	static SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent> predictor;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
		Context<RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1, data._2);
		RecsysBooleanFeatureExtractor featureExtractor = new RecsysBooleanFeatureExtractor(data._1());
		predictor = new SpaceAlignmentRecommender<>(context, featureExtractor, r, neighbourhoodSize, loader.getJavaSparkContext());
		predictor.train();
	}

	@Test
	public void recommendTest() {
		int userId = 2;
		List<? extends Recommendation> prediction = predictor.recommend(userId,
				START_TIME.plusHours(19), numberOfResults);
		assertEquals(numberOfResults, prediction.size());
	}

	@AfterClass
	public static void tearDownOnce() {
		data._2().close();
	}
}
