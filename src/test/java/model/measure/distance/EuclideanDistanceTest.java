package model.measure.distance;

import static org.junit.Assert.assertEquals;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.junit.Test;

public class EuclideanDistanceTest {
	
	static EuclideanDistance distance = EuclideanDistance.instance();
	
	@Test
	public void calculateDistanceWithDenseZeroVectorTest() {
		DenseVector v = new DenseVector(new double[]{3.0d,4.0d});
		DenseVector zero = new DenseVector(new double[2]);
		double expectedDistance = 5.0d;
		double actualDistance = distance.calculate(v, zero);
		double actualDistanceInverse = distance.calculate(zero, v);
		assertEquals(expectedDistance, actualDistance, 0.0001d);
		assertEquals(expectedDistance, actualDistanceInverse, 0.0001d);
	}
	
	@Test
	public void calculateDistanceWithSparseZeroVectorTest() {
		SparseVector zero = new SparseVector(2, new int[]{}, new double[]{});
		SparseVector v = new SparseVector(2, new int[]{0, 1}, new double[]{3.0d, 4.0d});
		double expectedDistance = 5.0d;
		double actualDistance = distance.calculate(v, zero);
		double actualDistanceInverse = distance.calculate(zero, v);
		assertEquals(expectedDistance, actualDistance, 0.0001d);
		assertEquals(expectedDistance, actualDistanceInverse, 0.0001d);
	}
	
	@Test
	public void calculateDistanceSameSparseVectorTest() {
		SparseVector v1 = new SparseVector(2, new int[]{0, 1}, new double[]{3.0d, 4.0d});
		SparseVector v2 = new SparseVector(2, new int[]{0, 1}, new double[]{3.0d, 4.0d});
		double expectedDistance = 0.0d;
		double actualDistance = distance.calculate(v1, v2);
		assertEquals(expectedDistance, actualDistance, 0.0001d);
	}
	
	@Test
	public void calculateDistanceSameDenseVectorTest() {
		DenseVector v1 = new DenseVector(new double[]{3.0d, 4.0d});
		DenseVector v2 = new DenseVector(new double[]{3.0d, 4.0d});
		double expectedDistance = 0.0d;
		double actualDistance = distance.calculate(v1, v2);
		assertEquals(expectedDistance, actualDistance, 0.0001d);
	}
	
	@Test
	public void calculateDistanceSparseVectorTest() {
		SparseVector v1 = new SparseVector(2, new int[]{0, 1}, new double[]{1.0d, 1.0d});
		SparseVector v2 = new SparseVector(2, new int[]{0, 1}, new double[]{4.0d, 5.0d});
		double expectedDistance = 5.0d;
		double actualDistance = distance.calculate(v1, v2);
		double actualDistanceInverse = distance.calculate(v2, v1);
		assertEquals(expectedDistance, actualDistance, 0.0001d);
		assertEquals(expectedDistance, actualDistanceInverse, 0.0001d);
	}
	
	@Test
	public void calculateDistanceDenseVectorTest() {
		DenseVector v1 = new DenseVector(new double[]{1.0d, 1.0d});
		DenseVector v2 = new DenseVector(new double[]{4.0d, 5.0d});
		double expectedDistance = 5.0d;
		double actualDistance = distance.calculate(v1, v2);
		double actualDistanceInverse = distance.calculate(v2, v1);
		assertEquals(expectedDistance, actualDistance, 0.0001d);
		assertEquals(expectedDistance, actualDistanceInverse, 0.0001d);
	}
}
