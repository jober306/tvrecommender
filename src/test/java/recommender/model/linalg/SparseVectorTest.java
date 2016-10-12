package recommender.model.linalg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import recommender.model.linalg.SparseVector.SparseVectorEntry;
import scala.Tuple2;

public class SparseVectorTest {

	private static final double[] DATA = { 0, 1, 2, 0, 3, 4, 5, 0 };
	SparseVector v;

	@Before
	public void setUp() {
		v = new SparseVector(DATA);
	}

	@Test
	public void sparseVectorConstructedCorrectlyTest() {
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
		assertTrue(v.getLength() == DATA.length);
	}

	@Test
	public void getValueTest() {
		for (int i = 0; i < DATA.length; i++) {
			assertTrue(DATA[i] == v.getValue(i));
		}
	}

	@Test
	public void getAllIndexesValuesTest() {
		List<Tuple2<Integer, Double>> expectedIndexesValues = new ArrayList<Tuple2<Integer, Double>>();
		expectedIndexesValues.add(new Tuple2<Integer, Double>(1, 1.d));
		expectedIndexesValues.add(new Tuple2<Integer, Double>(2, 2.d));
		expectedIndexesValues.add(new Tuple2<Integer, Double>(4, 3.d));
		expectedIndexesValues.add(new Tuple2<Integer, Double>(5, 4.d));
		expectedIndexesValues.add(new Tuple2<Integer, Double>(6, 5.d));
		List<Tuple2<Integer, Double>> indexesValues = v.getAllIndexesValues();
		for (int i = 0; i < expectedIndexesValues.size(); i++) {
			assertEquals(expectedIndexesValues.get(i)._1,
					indexesValues.get(i)._1);
			assertEquals(expectedIndexesValues.get(i)._2,
					indexesValues.get(i)._2, 0.0d);
		}
	}

	@Test
	public void setEntryBiggerThanLengthTest() {
		assertTrue(!v.setEntry(10, 2.d));
		assertTrue(!v.setEntry(v.length, 2.0d));
		getValueTest();
	}

	@Test
	public void setEntryWithNonZeroTest() {
		int expectedLength = v.length;
		v.setEntry(1, 3.0d);
		assertEquals(expectedLength, v.length);
		assertEquals(3.0d, v.getValue(1), 0.0d);
		assertEquals(5, v.getAllIndexesValues().size());
		v.setEntry(0, 1.0d);
		assertEquals(expectedLength, v.length);
		assertEquals(1.0d, v.getValue(0), 0.0d);
		assertEquals(6, v.getAllIndexesValues().size());
	}

	@Test
	public void removeEntryTest() {
		assertTrue(!v.removeEntry(0));
		assertTrue(!v.removeEntry(v.length));
		assertEquals(5, v.getAllIndexesValues().size());
		assertTrue(v.removeEntry(1));
		assertEquals(0.0d, v.getValue(1), 0.0d);
		assertEquals(4, v.getAllIndexesValues().size());
	}

	@Test
	public void setEntryWithZeroTest() {
		assertTrue(!v.setEntry(0, 0.0d));
		assertEquals(5, v.getAllIndexesValues().size());
		assertTrue(v.setEntry(5, 0.0d));
		assertEquals(0.0d, v.getValue(5), 0.0d);
		assertEquals(4, v.getAllIndexesValues().size());
	}

	@Test
	public void getCompactRepresentationTest() {
		double[] data = { 0, 1, 2, 0, 3, 4, 5, 0 };
		SparseVector v = new SparseVector(data);
		double[] compactV = v.getDenseRepresentation();
		assertArrayEquals(data, compactV, 0.0d);
	}

	@After
	public void tearDown() {
		v = null;
	}
}
