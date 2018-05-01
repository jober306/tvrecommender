package data.recsys.feature;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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

public class RecsysBooleanFeatureExtractorTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";

	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static public RecsysBooleanFeatureExtractor featureExtractor;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUp() {
		sc = SparkUtilities.getADefaultSparkContext();
		data = new RecsysTVDataSetLoader(path, sc).loadDataSet();
		featureExtractor = new RecsysBooleanFeatureExtractor(data._1);
	}

	@Test
	public void mapSizeTest() {
		int expectedGenreSize = 8;
		int expectedSubgenreSize = 114;
		int expectedSlotSize = 168;
		assertThat(data._2.allChannelIds().size(), equalTo(featureExtractor.getChannelIDMap().size()));
		assertThat(expectedSlotSize, equalTo(featureExtractor.getSlotIDMap().size()));
		assertThat(expectedGenreSize, equalTo(featureExtractor.getGenreIDMap().size()));
		assertThat(expectedSubgenreSize, equalTo(featureExtractor.getSubgenreIDMap().size()));
	}

	@Test
	public void extractFeaturesFromProgramTest() {
		int channelID = 175;
		byte genreID = 3;
		byte subgenreID = 26;
		short slot = 2;
		RecsysTVProgram program = new RecsysTVProgram((short) 1, slot, channelID, 3, genreID, subgenreID);
		Vector features = featureExtractor.extractFeaturesFromProgram(program);
		int mappedChannelIndex = featureExtractor.getChannelIDMap().get(channelID);
		int mappedSlotIndex = featureExtractor.getSlotIDMap().get(slot);
		int mappedGenreIndex = featureExtractor.getGenreIDMap().get(genreID);
		int mappedSubgenreIndex = featureExtractor.getSubgenreIDMap().get(subgenreID);
		for (int i = 0; i < features.size(); i++) {
			double expectedValue = 0.0d;
			if (i == mappedChannelIndex || i == mappedGenreIndex || i == mappedSubgenreIndex || i == mappedSlotIndex) {
				expectedValue = 1;
			}
			assertThat(expectedValue, equalTo(features.apply(i)));
		}
	}

	@Test
	public void extractFeaturesFromEventTest() {
		int channelID = 175;
		short slot = 2;
		byte genreID = 3;
		byte subgenreID = 26;
		RecsysTVEvent event = new RecsysTVEvent(channelID, slot, (byte) 0, genreID, subgenreID, 100, 200, 34, 23);
		Vector features = featureExtractor.extractFeaturesFromEvent(event);
		int mappedChannelIndex = featureExtractor.getChannelIDMap().get(channelID);
		int mappedSlotIndex = featureExtractor.getSlotIDMap().get(slot);
		int mappedGenreIndex = featureExtractor.getGenreIDMap().get(genreID);
		int mappedSubgenreIndex = featureExtractor.getSubgenreIDMap().get(
				subgenreID);
		for (int i = 0; i < features.size(); i++) {
			double expectedValue = 0.0d;
			if (i == mappedChannelIndex || i == mappedGenreIndex || i == mappedSubgenreIndex || i == mappedSlotIndex) {
				expectedValue = 1;
			}
			assertThat(expectedValue, equalTo(features.apply(i)));
		}
	}

	@AfterClass
	public static void tearDown() {
		sc.close();
	}
}
