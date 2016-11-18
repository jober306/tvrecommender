package mllib.recommender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import mllib.model.DistributedUserItemMatrix;

import org.apache.commons.math3.util.Pair;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;

public class SpaceAlignmentPredictorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final static int r = 2;
	static DistributedUserItemMatrix R;
	static IndexedRowMatrix C;
	static RecsysTVDataSet dataSet;
	static SpaceAlignmentRecommender predictor;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		dataSet = loader.loadDataSet();
		R = dataSet.convertToDistUserItemMatrix();
		C = dataSet.getContentMatrix();
		predictor = new SpaceAlignmentRecommender(R, r, C, loader.getJavaSparkContext());
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
	
	@Test
	public void recommendTest(){
		int userId = 2;
		int numberOfResults =2;
		int n = 2;
		Vector[] newItems = new Vector[]{Vectors.dense(new double[] { 46, 19, 5, 81 }),
		Vectors.dense(new double[] { 30000, 100000, 488888, 29199}),
		Vectors.dense(new double[] { 54, 18, 10, 78 }),
		Vectors.dense(new double[] { 200, 29, 25, 11 })};
		int[] prediction = predictor.recommend(userId, numberOfResults, newItems, n);
		assertEquals(numberOfResults, prediction.length);
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
