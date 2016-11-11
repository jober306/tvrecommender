package mllib.recommender;

import javax.swing.text.AbstractDocument.Content;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.junit.BeforeClass;
import org.junit.Test;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import mllib.model.DistributedUserItemMatrix;
import spark.utilities.SparkUtilities;

public class SpaceAlignmentPredictorTest {
	
	final static int r = 2;
	static DistributedUserItemMatrix R;
	static IndexedRowMatrix C;
	static JavaSparkContext sc;
	
	@BeforeClass
	public static void setUp(){
		sc = SparkUtilities.getADefaultSparkContext();
		sc.setLogLevel("ERROR");
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		System.out.println("LOADING DATA SET");
		RecsysTVDataSet dataSet = loader.loadDataSet();
		System.out.println("DONE!\n");
		System.out.println("SPLITTING DATA SET");
		RecsysTVDataSet subSampleDataSet = dataSet.splitDataRandomly(new double[]{0.00001, 0.99999})[0];
		System.out.println("DONE!\n");
		System.out.println("CALCULATING MATRIX R");
		R = subSampleDataSet.convertToDistUserItemMatrix();
		System.out.println("Rating matrix dim: " + R.getNumRows() + " " + R.getNumCols());
		System.out.println("DONE!\n");
		System.out.println("CALCULATING MATRIX C");
		C = subSampleDataSet.getContentMatrix();
		System.out.println("Content matrix dim: " + C.numRows() + " " + C.numCols());
		System.out.println("DONE!\n");
	}
	
	@Test
	public void spaceAlignmentPredictorMprimeConstructedTest(){
		System.out.println("CALCULATING MATRIX M prime");
		SpaceAlignmentPredictor predictor = new SpaceAlignmentPredictor(R, r, C, sc);
		System.out.println("DONE!\n");
		for(int i = 0; i < 100; i++){
			System.out.print("Similarity with item number " + i + ": ");
			System.out.println(predictor.predictItemsSimilarity(Vectors.dense(new double[]{46,19,5,81}), i));
		}
		System.out.println("DONE!\n");
	}
	
	
}
