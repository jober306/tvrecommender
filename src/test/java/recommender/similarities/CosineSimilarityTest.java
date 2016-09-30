package recommender.similarities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CosineSimilarityTest {
	
	double epsilon = 0.001;
	
	@Test
	public void cosineSimilarityTest(){
		CosineSimilarity cosSim = CosineSimilarity.getInstance();
		double[] v1 = {1,2,3,4};
		double[] v2 = {2,2,1,4};
		double[] v3 = {-1,-2,-3,-4};
		Double expectedResult = 0.9128;
		assertEquals(expectedResult, cosSim.calculateSimilarity(v1, v2), epsilon);
		assertEquals(1.0d, cosSim.calculateSimilarity(v1, v1), epsilon);
		assertEquals(-1.0d, cosSim.calculateSimilarity(v1, v3), epsilon);
	}
}
