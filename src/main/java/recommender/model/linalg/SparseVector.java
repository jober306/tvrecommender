package recommender.model.linalg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import recommender.model.linalg.SparseVector.SparseVectorEntry;
import scala.Tuple2;

/**
 * Class that represents a vector in sparse representation, i.e. a vector
 * represented by a linked list where each nodes contains the index and the
 * value of the vector entry. Other entries are considered to be zeros.
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
	}

	/**
	 * Method that checks if the vector is empty (that means it contains only
	 * the null element).
	 * 
	 * @return True if the vector is empty true otherwise.
	 */
	public boolean isEmpty() {
		return vector.isEmpty();
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
			if (entry.index == index) {
				return entry.value;
			} else if (entry.index > index) {
				break;
			}
		}
		return 0;
	}

	public List<Tuple2<Integer, Double>> getAllIndexesValues() {
		List<Tuple2<Integer, Double>> indexesValues = new ArrayList<Tuple2<Integer, Double>>();
		for (SparseVectorEntry entry : vector) {
			indexesValues.add(new Tuple2<Integer, Double>(entry.index,
					entry.value));
		}
		return indexesValues;
	}

	public boolean setEntry(int index, double value) {
		if (index >= length) {
			return false;
		}
		ListIterator<SparseVectorEntry> it = vector.listIterator();
		while (it.hasNext()) {
			SparseVectorEntry entry = it.next();
			if (entry.index == index) {
				it.remove();
				it.add(new SparseVectorEntry(index, value));
				return true;
			} else if (entry.index > index) {
				it.previous();
				it.add(new SparseVectorEntry(index, value));
				return true;
			}
		}
		it.add(new SparseVectorEntry(index, value));
		return true;
	}

	public boolean removeEntry(int index) {
		Iterator<SparseVectorEntry> it = vector.iterator();
		while (it.hasNext()) {
			SparseVectorEntry entry = it.next();
			if (entry.index == index) {
				it.remove();
				return true;
			}
			if (entry.index > index) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Method that returns the compact representation of this vector in sparse
	 * representation.
	 * 
	 * @return The array of double representing the entries of the vector.
	 */
	public double[] getDenseRepresentation() {
		double[] denseVector = new double[length];
		// Only need to parse all of the node because the array is initialized
		// with 0s by default.
		for (SparseVectorEntry entry : vector) {
			denseVector[entry.index] = entry.value;
		}
		return denseVector;
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
