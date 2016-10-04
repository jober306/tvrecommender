package recommender.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import recommender.similarities.Similarity;
import recommender.similarities.SumSimilarity;

public class ItemSimilaritiesMatrixTest {

	private static final double[][] DATA = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 },
			{ 9, 10, 11, 12 } };
	private static final Similarity SUM_SIM = SumSimilarity.getInstance();

	ItemSimilaritiesMatrix V;

	@Before
	public void setUp() {
		UserItemMatrix X = new UserItemMatrix(DATA);
		V = new ItemSimilaritiesMatrix(X, SUM_SIM);
	}

	@Test
	public void entriesCalculatedCorrectlyTest() {
		double[][] expectedData = { { 30, 33, 36, 39 }, { 33, 36, 39, 42 },
				{ 36, 39, 42, 45 }, { 39, 42, 45, 48 } };
		for (int row = 0; row < V.getNumberOfRow(); row++) {
			for (int col = 0; col < V.getNumberOfCol(); col++) {
				assertTrue(expectedData[row][col] == V.getSimilarityValue(row,
						col));
			}
		}
	}
}
