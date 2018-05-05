package data.recsys.loader;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.Tuple2;
import util.spark.SparkUtilities;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class RecsysTVDataSetLoaderTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final RecsysTVEvent tvEvent1InMock = new RecsysTVEvent((short) 46,
			(short) 19, (byte) 1, (byte) 5, (byte) 81, 1, 202344, 50880093, 5);
	final RecsysTVEvent tvEvent2InMock = new RecsysTVEvent((short) 174,
			(short) 7, (byte) 1, (byte) 6, (byte) 11, 3, 109509, 51122125, 6);
	final RecsysTVEvent tvEvent3InMock = new RecsysTVEvent((short) 6,
			(short) 12, (byte) 1, (byte) 4, (byte) 30, 3, 5785, 51097405, 5);
	final RecsysTVProgram tvProgram1InMock = new RecsysTVProgram(tvEvent1InMock);
	final RecsysTVProgram tvProgram2InMock = new RecsysTVProgram(tvEvent2InMock);
	final RecsysTVProgram tvProgram3InMock = new RecsysTVProgram(tvEvent3InMock);


	static RecsysTVDataSetLoader loader;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUp() {
		sc = SparkUtilities.getADefaultSparkContext();
		loader = new RecsysTVDataSetLoader(path, sc);
		data = loader.loadDataSet();
	}

	@Test
	public void loadedDataSetNotEmptyTest() {
		RecsysTVDataSet dataSet = data._2();
		assertTrue(!dataSet.isEmpty());
	}

	@Test
	public void loadedDataCorrectlyTest() {
		RecsysTVDataSet dataSet = data._2();
		assertTrue(dataSet.contains(tvEvent1InMock));
		assertTrue(dataSet.contains(tvEvent2InMock));
		assertTrue(dataSet.contains(tvEvent3InMock));
	}
	
	@Test
	public void loadedEPGCorrectlyTest() {
		RecsysEPG epg = data._1();
		List<RecsysTVProgram> epgTVPrograms = epg.tvPrograms().collect();
		assertTrue(epgTVPrograms.contains(tvProgram1InMock));
		assertTrue(epgTVPrograms.contains(tvProgram1InMock));
		assertTrue(epgTVPrograms.contains(tvProgram1InMock));
	}

	@AfterClass
	public static void tearDown() {
		sc.close();
		loader = null;
	}
}
