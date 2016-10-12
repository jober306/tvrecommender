package recommender.model.linalg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import recommender.model.linalg.SparseVector.SparseVectorEntry;

public class SparseVectorTest {

	@Test
	public void sparseVectorConstructedCorrectlyTest() {
		double[] data = { 0, 1, 2, 0, 3, 4, 5, 0 };
		SparseVector v = new SparseVector(data);
		int[] expectedIndex = { 1, 2, 4, 5, 6 };
		double[] expectedValue = { 1, 2, 3, 4, 5 };
		int index = 0;
		for (SparseVectorEntry entry : v) {
			if (entry == null)
				break;
			assertTrue(expectedIndex[index] == entry.index);
			assertTrue(expectedValue[index] == entry.value);
			index++;
		}
	}

	@Test
	public void getLengthTest() {
		double[] data = { 0, 1, 2, 0, 3, 4, 5, 0 };
		SparseVector v = new SparseVector(data);
		assertTrue(v.getLength() == data.length);
	}

	@Test
	public void getValueTest() {
		double[] data = { 0, 1, 2, 0, 3, 4, 5, 0 };
		SparseVector v = new SparseVector(data);
		for (int i = 0; i < data.length; i++) {
			assertTrue(data[i] == v.getValue(i));
		}
	}

	@Test
	public void getCompactRepresentationTest() {
		double[] data = { 0, 1, 2, 0, 3, 4, 5, 0 };
		SparseVector v = new SparseVector(data);
		double[] compactV = v.getDenseRepresentation();
		assertArrayEquals(data, compactV, 0.0d);
	}
}
