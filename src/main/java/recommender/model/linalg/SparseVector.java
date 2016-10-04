package recommender.model.linalg;

import java.util.Iterator;
import java.util.LinkedList;

public class SparseVector {

	LinkedList<SparseVectorEntry> vector;

	public SparseVector(double[] compactVector) {
		vector = new LinkedList<SparseVectorEntry>();
		for (int i = 0; i < compactVector.length; i++) {
			if (compactVector[i] != 0) {
				vector.add(new SparseVectorEntry(i, compactVector[i]));
			}
		}
		vector.add(null);
	}

	public boolean isEmpty() {
		return vector.isEmpty();
	}

	public Iterator<SparseVectorEntry> getIterator() {
		return vector.iterator();
	}

	public class SparseVectorEntry {
		public int index;
		public double value;

		public SparseVectorEntry(int index, double value) {
			this.index = index;
			this.value = value;
		}
	}
}
