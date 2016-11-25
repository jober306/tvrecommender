package mllib.recommender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import scala.Tuple2;

public class SpaceAlignmentPredictorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final static int r = 2;
	static RecsysTVDataSet dataSet;
	static SpaceAlignmentRecommender<RecsysTVEvent> predictor;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		dataSet = loader.loadDataSet();
		predictor = new SpaceAlignmentRecommender<RecsysTVEvent>(dataSet, r);
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
		List<Tuple2<Integer, Double>> neighborhood = predictor
				.predictNewItemNeighborhoodForUser(newItem, userIndex, n);
		assertEquals(n, neighborhood.size());
		int[] itemIndexesSeenByUser = predictor.R.getItemIndexesSeenByUser(userIndex);
		for (int i = 0; i < n; i++) {
			Tuple2<Integer, Double> posValue = neighborhood.get(i);
			int pos = posValue._1();
			assertTrue(arrayContains(itemIndexesSeenByUser, pos));
		}
	}
	
	@Test
	public void predictNewItemNeighbourhoodTest(){
		Vector newItem = Vectors.dense(new double[] { 46, 19, 5, 81 });
		int n = 6;
		List<Tuple2<Integer, Double>> neighborhood = predictor
				.predictNewItemNeighbourhood(newItem, n);
		assertEquals(n, neighborhood.size());
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
