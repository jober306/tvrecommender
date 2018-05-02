package model.measure.similarity;

import static org.junit.Assert.assertEquals;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CosineSimilarityTest {
	
	static final double[] iValues = new double[]{0.0, 1, 2, 3};
	static final double[] jValues = new double[]{0.0, 0, 0, 2};	
	
	CosineSimilarity cosineSimilarity;
	DenseVector denseI;
	DenseVector denseJ;
	SparseVector sparseI;
	SparseVector sparseJ;
	
	@Before
	public void setUp(){
		cosineSimilarity = CosineSimilarity.instance();
		denseI = new DenseVector(iValues);
		denseJ = new DenseVector(jValues);
		sparseI = denseI.toSparse();
		sparseJ = denseJ.toSparse();
	}
	
	@Test
	public void calculateSparseVectorTest(){
		double expectedResult = 0.802;
		double actualResult = cosineSimilarity.calculate(sparseI, sparseJ);
		assertEquals(expectedResult, actualResult, 0.001);
	}
	
	@Test
	public void calculateDenseVectorTest(){
		double expectedResult = 0.802;
		double actualResult = cosineSimilarity.calculate(denseI, denseJ);
		assertEquals(expectedResult, actualResult, 0.001);
	}
	
	@After
	public void tearDown(){
		cosineSimilarity = null;
		denseI = null;
		denseJ = null;
		sparseI = null;
		sparseJ = null;
	}
}
