package mllib.recommender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mllib.model.DistributedUserItemMatrix;

import org.apache.commons.math3.util.Pair;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.utilities.SparkUtilities;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;

public class SpaceAlignmentPredictorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final static int r = 2;
	static DistributedUserItemMatrix R;
	static IndexedRowMatrix C;
	static JavaSparkContext sc;
	static RecsysTVDataSet dataSet;
	static SpaceAlignmentPredictor predictor;

	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		dataSet = loader.loadDataSet();
		R = dataSet.convertToDistUserItemMatrix();
		C = dataSet.getContentMatrix();
		predictor = new SpaceAlignmentPredictor(R, r, C, sc);
	}

	@Test
	public void predictItemSimilarityTest() {
		for (int i = 0; i < 5; i++) {
			double similarity = predictor.predictItemsSimilarity(
					Vectors.dense(new double[] { 46, 19, 5, 81 }), i);
			assertTrue(similarity >= 0);
			assertTrue(similarity <= 1);
		}
	}

	@Test
	public void predictNewItemNeighborhoodForUserTest() {
		Vector newItem = Vectors.dense(new double[] { 46, 19, 5, 81 });
		int userIndex = 2;
		int n = 1;
		List<Pair<Integer, Double>> neighborhood = predictor
				.predictNewItemNeighborhoodForUser(newItem, userIndex, n);
		assertEquals(n, neighborhood.size());
		int[] itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		for (int i = 0; i < n; i++) {
			Pair<Integer, Double> posValue = neighborhood.get(i);
			int pos = posValue.getFirst();
			double value = posValue.getSecond();
			assertTrue(arrayContains(itemIndexesSeenByUser, pos));
			assertTrue(value >= 0);
			assertTrue(value <= 1);
		}
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
		dataSet.close();
	}
}
