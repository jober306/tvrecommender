package mllib.recommender;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.eclipse.jetty.util.statistic.SampleStatistic;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import mllib.model.DistributedUserItemMatrix;
import spark.utilities.SparkUtilities;

public class SpaceAlignmentPredictorTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	final static int r = 2;
	static DistributedUserItemMatrix R;
	static IndexedRowMatrix C;
	static JavaSparkContext sc;
	static RecsysTVDataSet dataSet;
	
	@BeforeClass
	public static void setUpOnce(){
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		dataSet = loader.loadDataSet();
		R = dataSet.convertToDistUserItemMatrix();
		C = dataSet.getContentMatrix();
	}
	
	@Test
	public void spaceAlignmentPredictorMprimeConstructedTest(){
		SpaceAlignmentPredictor predictor = new SpaceAlignmentPredictor(R, r, C, sc);
		for(int i = 0; i < 5; i++){
			System.out.print("Similarity with item number " + i + ": ");
			System.out.println(predictor.predictItemsSimilarity(Vectors.dense(new double[]{46,19,5,81}), i));
		}
	}
	
	@AfterClass
	public static void tearDownOnce(){
		dataSet.close();
	}
}
