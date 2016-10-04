package recommender.similarities;

import recommender.model.linalg.SparseVector;

/**
 * Interface that forces a class to implements methods to calculate similarity
 * between vectors in both compact and sparse representation.
 * 
 * @author Jonathan Bergeron.
 *
 */
public interface Similarity {

	public double calculateSimilarity(double[] vector1, double[] vector2);

	public double calculateSimilarity(SparseVector v1, SparseVector v2);
}
