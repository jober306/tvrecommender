package model.measure;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NormalizedMeasureTest {

	static final double[] iValues = new double[]{0.0, 1, 2, 3};
	static final double iNorm = 3.742;
	static final double[] jValues = new double[]{0.0, 0, 0, 2};
	static final double jNorm = 2;
	
	
	NormalizedMeasure normalizedVectorSum;
	DenseVector denseI;
	DenseVector denseJ;
	SparseVector sparseI;
	SparseVector sparseJ;
	
	@Before
	public void setUp(){
		normalizedVectorSum = new NormalizedMeasure(VectorSum.instance());
		denseI = new DenseVector(iValues);
		denseJ = new DenseVector(jValues);
		sparseI = denseI.toSparse();
		sparseJ = denseJ.toSparse();
	}
	
	@Test
	public void calculateSparseVectorTest(){
		double iNormalizedSum = Arrays.stream(iValues).map(v -> v / iNorm).sum();
		double jNormalizedSum = Arrays.stream(jValues).map(v -> v / jNorm).sum();
		double expectedResult = iNormalizedSum + jNormalizedSum;
		double actualResult = normalizedVectorSum.calculate(denseI, denseJ);
		assertEquals(expectedResult, actualResult, 0.001d);
	}
	
	@Test
	public void calculateDenseVectorTest(){
		double iNormalizedSum = Arrays.stream(iValues).map(v -> v / iNorm).sum();
		double jNormalizedSum = Arrays.stream(jValues).map(v -> v / jNorm).sum();
		double expectedResult = iNormalizedSum + jNormalizedSum;
		double actualResult = normalizedVectorSum.calculate(sparseI, sparseJ);
		assertEquals(expectedResult, actualResult, 0.001d);
	}
	
	@After
	public void tearDown(){
		normalizedVectorSum = null;
		denseI = null;
		denseJ = null;
		sparseI = null;
		sparseJ = null;
	}
}
