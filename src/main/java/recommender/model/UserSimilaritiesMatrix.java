package recommender.model;

import recommender.model.linalg.SparseVector;
import recommender.similarities.Similarity;

/**
 * Class the represents an user similarity matrix.
 * 
 * @author Jonathan Bergeron
 *
 */
public class UserSimilaritiesMatrix extends SimilarityMatrix {

	/**
	 * Constructor of the class. It uses an user item matrix and a similarity to
	 * calculate its entries.
	 * 
	 * @param userItemMatrix
	 *            The <class>UserItemMatrix</class> from which the similarities
	 *            are calculated.
	 * @param similarity
	 *            The similarity to be used.
	 */
	public UserSimilaritiesMatrix(UserItemMatrix userItemMatrix,
			Similarity similarity) {
		this.similarity = similarity;
		calculateUserSimilaritiesMatrix(userItemMatrix);
	}

	private void calculateUserSimilaritiesMatrix(UserItemMatrix userItemMatrix) {
		int nbUsers = userItemMatrix.getNumberOfUsers();
		similaritiesMatrix = new double[nbUsers][nbUsers];
		SparseVector[] usersSparse = userItemMatrix
				.getUsersInSparseVectorRepresentation();
		for (int i = 0; i < nbUsers; i++) {
			for (int j = 0; j < nbUsers; j++) {
				similaritiesMatrix[i][j] = similarity.calculateSimilarity(
						usersSparse[i], usersSparse[j]);
			}
		}
	}
}
