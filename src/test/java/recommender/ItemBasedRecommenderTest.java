package recommender;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.ItemBasedRecommender;
import scala.Tuple2;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;

public class ItemBasedRecommenderTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static ItemBasedRecommender<RecsysTVProgram, RecsysTVEvent> recommender;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;

	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
		recommender = new ItemBasedRecommender<RecsysTVProgram, RecsysTVEvent>(
				data._1(), data._2());
		recommender.train();
	}

	@Test
	public void predictItemNeighborhoodForUserTest() {
		int userIndex = 2;
		int itemIndex = 4;
		int n = 16;
		List<Tuple2<Integer, Double>> neighborhood = recommender
				.predictItemNeighbourhoodForUser(userIndex, itemIndex, n);
		int[] itemIndexesSeenByUser = recommender.R
				.getItemIndexesSeenByUser(userIndex);
		for (int i = 0; i < neighborhood.size(); i++) {
			Tuple2<Integer, Double> posValue = neighborhood.get(i);
			int pos = posValue._1();
			double value = posValue._2();
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
		data._2().close();
	}
}
