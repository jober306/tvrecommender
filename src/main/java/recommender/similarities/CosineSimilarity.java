package recommender.similarities;

import java.util.Iterator;

import recommender.model.linalg.SparseVector;
import recommender.model.linalg.SparseVector.SparseVectorEntry;

/**
 * Singleton class that calculates cosine similarity between two vectors.
 * 
 * @author Jonathan Bergeron
 *
 */
public class CosineSimilarity implements Similarity {

	private static CosineSimilarity COSINE_SIMILARITY = new CosineSimilarity();

	private CosineSimilarity() {
	};

	/**
	 * Method that gives access to the singleton CosineSimilarity.
	 * 
	 * @return The singleton CosineSimilarity object.
	 */
	public static CosineSimilarity getInstance() {
		return COSINE_SIMILARITY;
	}

	@Override
	/**
	 * Method that calculate the cosine similarity between two vectors.
	 * They must be the same size.
	 * 
	 * @param vector1 The first vector.
	 * @param vector2 The second vector.
	 * @return The cosine similarity between vector1 and vector2
	 */
	public double calculateSimilarity(double[] vector1, double[] vector2) {
		float dotProduct = 0.0f;
		float norm1 = 0.0f;
		float norm2 = 0.0f;
		for (int i = 0; i < vector1.length; i++) {
			dotProduct += vector1[i] * vector2[i];
			norm1 += vector1[i] * vector1[i];
			norm2 += vector2[i] * vector2[i];
		}
		return (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2)));
	}

	/**
	 * Method that calculate the cosine similarity between two vectors in sparse
	 * representation. They must be the same size.
	 * 
	 * @param vec1
	 *            The fist vector.
	 * @param vec2
	 *            The second vector.
	 * @return The cosine similarity between vector1 and vector2
	 */
	public double calculateSimilarity(SparseVector vec1, SparseVector vec2) {
		if (vec1.isEmpty() || vec2.isEmpty()) {
			return 0.0d;
		}
		Iterator<SparseVectorEntry> it1 = vec1.iterator();
		Iterator<SparseVectorEntry> it2 = vec2.iterator();
		float dotProduct = 0.0f;
		float norm1 = 0.0f;
		float norm2 = 0.0f;
		SparseVectorEntry entry1 = it1.next();
		SparseVectorEntry entry2 = it2.next();
		while (true) {
			if (entry1.index == entry2.index) {
				dotProduct += entry1.value * entry2.value;
				norm1 += entry1.value * entry1.value;
				norm2 += entry2.value * entry2.value;
				if (it1.hasNext() && it2.hasNext()) {
					entry1 = it1.next();
					entry2 = it2.next();
				} else {
					break;
				}
			} else if (entry1.index < entry2.index) {
				norm1 += entry1.value * entry1.value;
				if (it1.hasNext()) {
					entry1 = it1.next();
				} else {
					norm2 += entry2.value * entry2.value;
					break;
				}
			} else {
				norm2 += entry2.value * entry2.value;
				if (it2.hasNext()) {
					entry2 = it2.next();
				} else {
					norm1 += entry1.value * entry1.value;
					break;
				}
			}
		}
		while (it1.hasNext()) {
			entry1 = it1.next();
			norm1 += entry1.value * entry1.value;
		}
		while (it2.hasNext()) {
			entry2 = it2.next();
			norm2 += entry2.value * entry2.value;
		}
		return (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2)));
	}
}
