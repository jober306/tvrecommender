package mllib.recommender.collaborativefiltering;

import static org.junit.Assert.assertTrue;

import java.util.List;

import mllib.model.DistributedUserItemMatrix;

import org.apache.commons.math3.util.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;

public class ItemBasedRecommenderTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static DistributedUserItemMatrix R;
	static ItemBasedRecommender<RecsysTVEvent> recommender;
	static RecsysTVDataSet dataSet;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		dataSet = loader.loadDataSet();
		recommender = new ItemBasedRecommender<RecsysTVEvent>(dataSet);
	}

	@Test
	public void predictItemNeighborhoodForUserTest() {
		int userIndex = 2;
		int itemIndex = 4;
		int n = 16;
		List<Pair<Integer, Double>> neighborhood = recommender
				.getItemNeighborhoodForUser(userIndex, itemIndex, n);
		int[] itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		for (int i = 0; i < neighborhood.size(); i++) {
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
