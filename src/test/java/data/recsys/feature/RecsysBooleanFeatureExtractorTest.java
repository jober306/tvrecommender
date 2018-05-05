package data.recsys.feature;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.Tuple2;
import util.spark.SparkUtilities;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.utility.RecsysUtilities;

public class RecsysBooleanFeatureExtractorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";

	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static public RecsysBooleanFeatureExtractor featureExtractor;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUp() {
		sc = SparkUtilities.getADefaultSparkContext();
		data = new RecsysTVDataSetLoader(path, sc).loadDataSet();
		featureExtractor = RecsysBooleanFeatureExtractor.instance();
	}

	@Test
	public void extractFeaturesFromProgramTest() {
		short channelID = 175;
		byte genreID = 3;
		byte subgenreID = 26;
		short slot = 2;
		RecsysTVProgram program = new RecsysTVProgram((short) 1, slot, channelID, 3, genreID, subgenreID);
		Vector features = featureExtractor.extractFeaturesFromProgram(program);
		int mappedChannelIndex = RecsysUtilities.getChannelIDMap().get(channelID);
		int mappedSlotIndex = RecsysUtilities.getSlotIDMap().get(slot);
		int mappedGenreIndex = RecsysUtilities.getGenreIDMap().get(genreID);
		int mappedSubgenreIndex = RecsysUtilities.getSubgenreIDMap().get(subgenreID);
		for (int i = 0; i < features.size(); i++) {
			double expectedValue = 0.0d;
			if (i == mappedChannelIndex || i == mappedGenreIndex || i == mappedSubgenreIndex || i == mappedSlotIndex) {
				expectedValue = 1;
			}
			assertEquals(expectedValue, features.apply(i), 0.0d);
		}
	}

	@Test
	public void extractFeaturesFromEventTest() {
		short channelID = 175;
		short slot = 2;
		byte genreID = 3;
		byte subgenreID = 26;
		RecsysTVEvent event = new RecsysTVEvent(channelID, slot, (byte) 0, genreID, subgenreID, 100, 200, 34, 23);
		Vector features = featureExtractor.extractFeaturesFromEvent(event);
		int mappedChannelIndex = RecsysUtilities.getChannelIDMap().get(channelID);
		int mappedSlotIndex = RecsysUtilities.getSlotIDMap().get(slot);
		int mappedGenreIndex = RecsysUtilities.getGenreIDMap().get(genreID);
		int mappedSubgenreIndex = RecsysUtilities.getSubgenreIDMap().get(
				subgenreID);
		for (int i = 0; i < features.size(); i++) {
			double expectedValue = 0.0d;
			if (i == mappedChannelIndex || i == mappedGenreIndex || i == mappedSubgenreIndex || i == mappedSlotIndex) {
				expectedValue = 1;
			}
			assertEquals(expectedValue, features.apply(i), 0.0d);
		}
	}
	
	@Test
	public void extractedVectorSizeTest(){
		int expectedSize = 507;
		int actualSize = featureExtractor.extractedVectorSize();
		assertEquals(expectedSize, actualSize);
	}

	@AfterClass
	public static void tearDown() {
		sc.close();
	}
}
