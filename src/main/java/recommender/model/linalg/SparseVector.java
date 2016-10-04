package recommender.model.linalg;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class that represents a vector in sparse representation, i.e. a vector
 * represented by a linked list where each nodes contains the index and the
 * value of the vector entry. Other entries are considered to be zeros. The last
 * node of the SparseVector is a sentinel one containing null.
 * 
 * @author Jonathan Bergeron
 *
 */
public class SparseVector {

	LinkedList<SparseVectorEntry> vector;

	/**
	 * Constructor of the class that builds the sparse representation given the
	 * compact representation of a vector.
	 * 
	 * @param compactVector
	 *            The compact representation of a vector.
	 */
	public SparseVector(double[] compactVector) {
		vector = new LinkedList<SparseVectorEntry>();
		for (int i = 0; i < compactVector.length; i++) {
			if (compactVector[i] != 0) {
				vector.add(new SparseVectorEntry(i, compactVector[i]));
			}
		}
		vector.add(null);
	}

	/**
	 * Method that checks if the vector is empty (that means it contains only
	 * the null element).
	 * 
	 * @return True if the vector is empty true otherwise.
	 */
	public boolean isEmpty() {
		return vector.size() == 1;
	}

	/**
	 * Method that return an iterator to iterate through the vector entries.
	 * 
	 * @return The iterator of <class>SparseVectorEntry</class> of this vector.
	 */
	public Iterator<SparseVectorEntry> getIterator() {
		return vector.iterator();
	}

	/**
	 * Class that represents an entry of the vector. It contains the index and
	 * the value of the entry.
	 * 
	 * @author Jonathan Bergeron
	 *
	 */
	public class SparseVectorEntry {

		/**
		 * The index of the entry in the original vector.
		 */
		public int index;

		/**
		 * The value of the entry.
		 */
		public double value;

		protected SparseVectorEntry(int index, double value) {
			this.index = index;
			this.value = value;
		}
	}
}
