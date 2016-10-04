package recommender.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import recommender.model.linalg.SparseVector;
import recommender.similarities.Similarity;

public class UserSimilaritiesMatrixTest {

	private static final double[][] DATA = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 },
			{ 9, 10, 11, 12 } };
	private static final Similarity SUM_SIM = new Similarity() {

		@Override
		public double calculateSimilarity(double[] vector1, double[] vector2) {
			double sum = 0.0d;
			for (int i = 0; i < vector1.length; i++) {
				sum += vector1[i];
				sum += vector2[i];
			}
			return sum;
		}

		@Override
		public double calculateSimilarity(SparseVector vector1,
				SparseVector vector2) {
			return 1;
		}
	};

	UserSimilaritiesMatrix U;

	@Before
	public void setUp() {
		UserItemMatrix X = new UserItemMatrix(DATA);
		U = new UserSimilaritiesMatrix(X, SUM_SIM);
	}

	@Test
	public void entriesCalculatedCorrectlyTest() {
		double[][] expectedData = { { 20, 36, 52 }, { 36, 52, 68 },
				{ 52, 68, 84 } };
		for (int row = 0; row < U.getNumberOfRow(); row++) {
			for (int col = 0; col < U.getNumberOfCol(); col++) {
				assertTrue(expectedData[row][col] == U.getSimilarityValue(row,
						col));
			}
		}
	}
}
