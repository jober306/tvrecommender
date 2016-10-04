package recommender.model.linalg;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import recommender.model.linalg.SparseVector.SparseVectorEntry;

public class SparseVectorTest {

	@Test
	public void sparseVectorConstructedCorrectlyTest() {
		double[] data = { 0, 1, 2, 0, 3, 4, 5, 0 };
		SparseVector v = new SparseVector(data);
		Iterator<SparseVectorEntry> it = v.getIterator();
		SparseVectorEntry entry1 = it.next();
		assertTrue(entry1.index == 1 && entry1.value == 1);
		SparseVectorEntry entry2 = it.next();
		assertTrue(entry2.index == 2 && entry2.value == 2);
		SparseVectorEntry entry3 = it.next();
		assertTrue(entry3.index == 4 && entry3.value == 3);
		SparseVectorEntry entry4 = it.next();
		assertTrue(entry4.index == 5 && entry4.value == 4);
		SparseVectorEntry entry5 = it.next();
		assertTrue(entry5.index == 6 && entry5.value == 5);
	}
}
