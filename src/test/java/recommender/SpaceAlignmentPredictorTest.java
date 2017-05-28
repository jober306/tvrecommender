package recommender;

import static data.recsys.RecsysTVDataSet.START_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.SpaceAlignmentRecommender;
import scala.Tuple2;
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
		RecsysBooleanFeatureExtractor featureExtractor = new RecsysBooleanFeatureExtractor(data._1());
		predictor = new SpaceAlignmentRecommender<RecsysTVProgram, RecsysTVEvent>(
				data._1(), data._2(), featureExtractor, r,
				neighbourhoodSize);
		predictor.train();
	}

	@Test
	public void predictItemSimilarityTest() {
		for (int i = 0; i < 5; i++) {
			double[] entries = new double[132];
			entries[2] = 1.0d;
			entries[13] = 1.0d;
			entries[123] = 1.0d;
			double similarity = predictor.predictItemsSimilarity(
					Vectors.dense(entries), i);
			assertTrue(similarity >= 0);
		}
	}

	@Test
	public void predictNewItemNeighborhoodForUserTest() {
		double[] entries = new double[132];
		entries[2] = 1.0d;
		entries[13] = 1.0d;
		entries[123] = 1.0d;
		Vector newItem = Vectors.dense(entries);
		int userIndex = 2;
		int n = 1;
		List<Tuple2<Integer, Double>> neighborhood = predictor
				.predictNewItemNeighborhoodForUser(newItem, userIndex, n);
		assertEquals(n, neighborhood.size());
		int[] itemIndexesSeenByUser = predictor.R
				.getItemIndexesSeenByUser(userIndex);
		for (int i = 0; i < n; i++) {
			Tuple2<Integer, Double> posValue = neighborhood.get(i);
			int pos = posValue._1();
			assertTrue(arrayContains(itemIndexesSeenByUser, pos));
		}
	}

	@Test
	public void predictNewItemNeighbourhoodTest() {
		double[] entries = new double[132];
		entries[2] = 1.0d;
		entries[13] = 1.0d;
		entries[123] = 1.0d;
		Vector newItem = Vectors.dense(entries);
		int n = 6;
		List<Tuple2<Integer, Double>> neighborhood = predictor
				.predictNewItemNeighbourhood(newItem, n);
		assertEquals(n, neighborhood.size());
	}

	@Test
	public void recommendTest() {
		int userId = 2;
		List<Integer> prediction = predictor.recommend(userId,
				START_TIME.plusHours(19), numberOfResults);
		assertEquals(numberOfResults, prediction.size());
	}

	private boolean arrayContains(int[] array, int value) {
		boolean contain = false;
		for (int arrayValue : array) {
			if (arrayValue == value) {
				contain = true;
				break;
			}
		}
		return contain;
	}

	@AfterClass
	public static void tearDownOnce() {
		data._2().close();
	}
}
