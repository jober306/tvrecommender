package recommender.similarities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import recommender.model.linalg.SparseVector;

public class SumSimilarityTest {

	static Similarity sumSim = SumSimilarity.getInstance();

	@Test
	public void calculateSimilarityCompactVectorTest() {
		double[] data1 = { 1, 3, 5, 6, 7 };
		double[] data2 = { 0, 1, 0, 1, 0 };
		double expectedAnswer = 24;
		assertEquals(expectedAnswer, sumSim.calculateSimilarity(data1, data2),
				0.0d);
	}

	@Test
	public void calculateSimilaritySparseVectorTest() {
		double[] data1 = { 1, 3, 5, 6, 7 };
		SparseVector v1 = new SparseVector(data1);
		double[] data2 = { 0, 1, 0, 1, 0 };
		SparseVector v2 = new SparseVector(data2);
		double expectedAnswer = 24;
		assertEquals(expectedAnswer, sumSim.calculateSimilarity(v1, v2), 0.0d);
	}
}
