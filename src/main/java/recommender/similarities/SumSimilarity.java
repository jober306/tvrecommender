package recommender.similarities;

import java.util.Iterator;

import recommender.model.linalg.SparseVector;
import recommender.model.linalg.SparseVector.SparseVectorEntry;

/**
 * Class that mock a similarity measure a bit complex by adding all the elements
 * of both vector.
 * 
 * @author Jonathan Bergeron
 *
 */
public class SumSimilarity implements Similarity {

	private static SumSimilarity SUM_SIMILARITY = new SumSimilarity();

	private SumSimilarity() {
	};

	/**
	 * Method that gives access to the singleton SumSimilarity.
	 * 
	 * @return The singleton CosineSimilarity object.
	 */
	public static SumSimilarity getInstance() {
		return SUM_SIMILARITY;
	}

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
	public double calculateSimilarity(SparseVector v1, SparseVector v2) {
		Iterator<SparseVectorEntry> it1 = v1.getIterator();
		Iterator<SparseVectorEntry> it2 = v2.getIterator();
		SparseVectorEntry entry1 = null;
		SparseVectorEntry entry2 = null;
		double sum = 0.0d;
		while ((entry1 = it1.next()) != null) {
			sum += entry1.value;
		}
		while ((entry2 = it2.next()) != null) {
			sum += entry2.value;
		}
		return sum;
	}
}
