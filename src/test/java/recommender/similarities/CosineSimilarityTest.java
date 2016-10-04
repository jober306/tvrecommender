package recommender.similarities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import recommender.model.linalg.SparseVector;

public class CosineSimilarityTest {

	double epsilon = 0.001;

	@Test
	public void cosineSimilarityWithCompactVectorTest() {
		CosineSimilarity cosSim = CosineSimilarity.getInstance();
		double[] v1 = { 1, 2, 3, 4 };
		double[] v2 = { 2, 2, 1, 4 };
		double[] v3 = { -1, -2, -3, -4 };
		Double expectedResult = 0.9128;
		assertEquals(expectedResult, cosSim.calculateSimilarity(v1, v2),
				epsilon);
		assertEquals(1.0d, cosSim.calculateSimilarity(v1, v1), epsilon);
		assertEquals(-1.0d, cosSim.calculateSimilarity(v1, v3), epsilon);
	}

	@Test
	public void cosineSimilarityWithSparseVectorTest() {
		CosineSimilarity cosSim = CosineSimilarity.getInstance();
		double[] data1 = { 1, 2, 3, 4 };
		SparseVector v1 = new SparseVector(data1);
		double[] data2 = { 2, 2, 1, 4 };
		SparseVector v2 = new SparseVector(data2);
		double[] data3 = { -1, -2, -3, -4 };
		SparseVector v3 = new SparseVector(data3);
		Double expectedResult = 0.9128;
		assertEquals(expectedResult, cosSim.calculateSimilarity(v1, v2),
				epsilon);
		assertEquals(1.0d, cosSim.calculateSimilarity(v1, v1), epsilon);
		assertEquals(-1.0d, cosSim.calculateSimilarity(v1, v3), epsilon);

		double[] data4 = { 0, 0, 1, 3, 0 };
		SparseVector v4 = new SparseVector(data4);
		double[] data5 = { 1, 0, 2, 0, 3 };
		SparseVector v5 = new SparseVector(data5);
		assertEquals(cosSim.calculateSimilarity(data4, data5),
				cosSim.calculateSimilarity(v4, v5), epsilon);
	}
}
