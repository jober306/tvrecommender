package recommender.model.linalg;

import java.util.Iterator;
import java.util.LinkedList;

import recommender.model.linalg.SparseVector.SparseVectorEntry;

/**
 * Class that represents a vector in sparse representation, i.e. a vector
 * represented by a linked list where each nodes contains the index and the
 * value of the vector entry. Other entries are considered to be zeros. The last
 * node of the SparseVector is a sentinel one containing null.
 * 
 * @author Jonathan Bergeron
 *
 */
public class SparseVector implements Iterable<SparseVectorEntry> {

	LinkedList<SparseVectorEntry> vector;
	int length;

	/**
	 * Constructor of the class that builds the sparse representation given the
	 * compact representation of a vector.
	 * 
	 * @param compactVector
	 *            The compact representation of a vector.
	 */
	public SparseVector(double[] compactVector) {
		length = compactVector.length;
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
	 * Method that returns the length of the vector.
	 * 
	 * @return The length of the vector.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Method that returns the value at the specified index.
	 * 
	 * @param index
	 *            The index.
	 * @return The value at the specified index.
	 */
	public double getValue(int index) {
		for (SparseVectorEntry entry : vector) {
			if (entry == null) {
				break;
			}
			if (entry.index == index) {
				return entry.value;
			} else if (entry.index > index) {
				break;
			}
		}
		return 0;
	}

	/**
	 * Method that returns the compact representation of this vector in sparse
	 * representation.
	 * 
	 * @return The array of double representing the entries of the vector.
	 */
	public double[] getCompactRepresentation() {
		double[] compactVector = new double[length];
		for (SparseVectorEntry entry : vector) {
			if (entry != null) {
				compactVector[entry.index] = entry.value;
			}
		}
		return compactVector;
	}

	/**
	 * Method that return an iterator to iterate through the vector entries.
	 * 
	 * @return The iterator of <class>SparseVectorEntry</class> of this vector.
	 */
	@Override
	public Iterator<SparseVectorEntry> iterator() {
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
