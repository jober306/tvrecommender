package recommender.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import recommender.similarities.Similarity;

public class SimilarityMatrixTest {

	private static final double[][] DATA = { { 1, 2, 3 }, { 4, 5, 6 } };
	private static final Similarity ONE_SIM = new Similarity() {

		@Override
		public double calculateSimilarity(double[] vector1, double[] vector2) {
			return 1;
		}
	};

	public class SimilarityMatrixMock extends SimilarityMatrix {

		public SimilarityMatrixMock(Similarity sim, double[][] similarities) {
			this.similarity = sim;
			similaritiesMatrix = new double[similarities.length][similarities[0].length];
			for (int row = 0; row < similarities.length; row++) {
				for (int col = 0; col < similarities[0].length; col++) {
					similaritiesMatrix[row][col] = similarities[row][col];
				}
			}
		}
	}

	SimilarityMatrixMock simMat;

	@Before
	public void setUp() {
		simMat = new SimilarityMatrixMock(ONE_SIM, DATA);
	}

	@Test
	public void getSimilarityValueTest() {
		for (int row = 0; row < DATA.length; row++) {
			for (int col = 0; col < DATA[0].length; col++) {
				assertTrue(DATA[row][col] == simMat
						.getSimilarityValue(row, col));
			}
		}
	}

	@Test
	public void getSimilarityTest() {
		assertTrue(ONE_SIM == simMat.getSimilarity());
	}

	@Test
	public void getNumberOfRowTest() {
		assertTrue(2 == simMat.getNumberOfRow());
	}

	@Test
	public void getNumberOfColTest() {
		assertTrue(3 == simMat.getNumberOfCol());
	}

	@Test
	public void getRowTest() {
		assertArrayEquals(DATA[0], simMat.getRow(0), 0.0d);
	}

	@Test
	public void getColumnTest() {
		double[] expectedArray = { 1, 4 };
		assertArrayEquals(expectedArray, simMat.getColumn(0), 0.0d);
	}

	@Test
	public void getTopNSimilarIndicesTest() {
		SimilarityMatrixMock bigSimMat = new SimilarityMatrixMock(ONE_SIM,
				generateMatrixrData(10, 10));
		List<Integer> expectedRowResult = Arrays.asList(9, 8, 7, 6, 5);
		List<Integer> expectedColResult = Arrays.asList(9, 8, 7, 6, 5);
		List<Integer> rowResult = bigSimMat.getTopNSimilarRowIndices(0, 5);
		List<Integer> colResult = bigSimMat.getTopNSimilarColumnIndices(3, 5);
		assertTrue(expectedRowResult.size() == rowResult.size());
		assertTrue(expectedColResult.size() == colResult.size());
		for (int i = 0; i < rowResult.size(); i++) {
			assertTrue(expectedRowResult.get(i) == rowResult.get(i));
			assertTrue(expectedColResult.get(i) == colResult.get(i));
		}
	}

	private double[][] generateMatrixrData(int numberOfRow, int numberOfCol) {
		double[][] data = new double[numberOfRow][numberOfCol];
		int value = 0;
		for (int row = 0; row < numberOfRow; row++) {
			for (int col = 0; col < numberOfCol; col++) {
				data[row][col] = value;
				value++;
			}
		}
		return data;
	}

	@Test
	@After
	public void tearDown() {
		simMat = null;
	}
}