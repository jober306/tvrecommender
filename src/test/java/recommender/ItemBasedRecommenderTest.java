package recommender;

import static org.junit.Assert.assertTrue;

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
import scala.Tuple2;
import util.spark.SparkUtilities;

public class ItemBasedRecommenderTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static ItemBasedRecommender<RecsysTVProgram, RecsysTVEvent> recommender;

	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		Tuple2<RecsysEPG, RecsysTVDataSet> data = loader.loadDataSet();
		Context<RecsysTVProgram, RecsysTVEvent> context = new Context<>(data._1, data._2);
		recommender = new ItemBasedRecommender<>(context, 10);
		recommender.train();
	}

	@Test
	public void predictItemNeighborhoodForUserTest() {
		int userIndex = 2;
		int itemIndex = 4;
		int n = 16;
		List<Tuple2<Integer, Double>> neighborhood = recommender.predictItemNeighbourhoodForUser(userIndex, itemIndex, n);
		List<Integer> itemIndexesSeenByUser = recommender.R.getItemIndexesSeenByUser(userIndex);
		for (int i = 0; i < neighborhood.size(); i++) {
			Tuple2<Integer, Double> posValue = neighborhood.get(i);
			int pos = posValue._1();
			double value = posValue._2();
			assertTrue(itemIndexesSeenByUser.contains(pos));
			assertTrue(value >= 0);
			assertTrue(value <= 1);
		}
	}

	@AfterClass
	public static void tearDownOnce() {
		recommender.closeContextDatasets();
		sc.close();
	}
}
