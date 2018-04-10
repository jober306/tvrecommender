package recommender;

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
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.loader.RecsysTVDataSetLoader;
import model.recommendation.Recommendations;
import model.recommendation.ScoredRecommendation;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class SpaceAlignmentPredictorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent> recommender;

	final static int r = 100;
	final static int numberOfResults = 2;
	final static int neighbourhoodSize = 2;
	
	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet();
		Context<RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1, data._2);
		RecsysBooleanFeatureExtractor featureExtractor = new RecsysBooleanFeatureExtractor(data._1());
		recommender = new SpaceAlignmentRecommender<>(context, numberOfResults, featureExtractor, r, neighbourhoodSize, loader.getJavaSparkContext());
		recommender.train();
	}

	@Test
	public void recommendTest() {
		int userId = 2;
		Recommendations<ScoredRecommendation> prediction = recommender.recommend(userId, START_TIME.plusHours(19));
		assertEquals(numberOfResults, prediction.size());
	}

	@AfterClass
	public static void tearDownOnce() {
		recommender.closeContextDatasets();
		sc.close();
	}
}
