package recommender.similarities;

import recommender.model.linalg.SparseVector;

/**
 * Class that mock a similarity measure and return always one.
 * 
 * @author Jonathan Bergeron
 *
 */
public class OneSimilarity implements Similarity {

	private static OneSimilarity ONE_SIMILARITY = new OneSimilarity();

	private OneSimilarity() {
	};

	/**
	 * Method that gives access to the singleton OneSimilarity.
	 * 
	 * @return The singleton CosineSimilarity object.
	 */
	public static OneSimilarity getInstance() {
		return ONE_SIMILARITY;
	}

	@Override
	public double calculateSimilarity(double[] vector1, double[] vector2) {
		return 1;
	}

	@Override
	public double calculateSimilarity(SparseVector v1, SparseVector v2) {
		return 1;
	}

}
