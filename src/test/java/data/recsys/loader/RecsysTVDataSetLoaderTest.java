package data.recsys.loader;

import static org.junit.Assert.assertTrue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.model.RecsysEPG;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import scala.Tuple2;

public class RecsysTVDataSetLoaderTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final RecsysTVEvent tvEvent1InMock = new RecsysTVEvent((short)46,(short)19,(byte)1,(byte)5,(byte)81,1,202344,50880093,5);
	final RecsysTVEvent tvEvent2InMock = new RecsysTVEvent((short)174,(short)7,(byte)1,(byte)6,(byte)11,3,109509,51122125,6);
	final RecsysTVEvent tvEvent3InMock = new RecsysTVEvent((short)6,(short)12,(byte)1,(byte)4,(byte)30,3,5785,51097405,5);
	
	static RecsysTVDataSetLoader loader;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	
	@BeforeClass
	public static void setUp(){
		loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
	}
	
	@Test
	public void loadedDataSetNotEmptyTest(){
		RecsysTVDataSet dataSet = data._2();
		assertTrue(!dataSet.isEmpty());
	}
	
	@Test
	public void loadedDataCorrectlyTest(){
		RecsysTVDataSet dataSet = data._2();
		assertTrue(dataSet.contains(tvEvent1InMock));
		assertTrue(dataSet.contains(tvEvent2InMock));
		assertTrue(dataSet.contains(tvEvent3InMock));
	}
	
	@AfterClass
	public static void tearDown(){
		RecsysTVDataSet dataSet = data._2();
		dataSet.close();
		loader = null;
	}
}
