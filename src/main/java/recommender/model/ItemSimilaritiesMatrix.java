package recommender.model;

import recommender.similarities.Similarity;

/**
 * Class the represents an item similarity matrix.
 * 
 * @author Jonathan Bergeron
 *
 */
public class ItemSimilaritiesMatrix extends SimilarityMatrix {

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
	public ItemSimilaritiesMatrix(UserItemMatrix userItemMatrix,
			Similarity similarity) {
		this.similarity = similarity;
		calculateItemSimilaritiesMatrix(userItemMatrix);
	}

	private void calculateItemSimilaritiesMatrix(UserItemMatrix userItemMatrix) {
		int nbItems = userItemMatrix.getNumberOfItems();
		similaritiesMatrix = new double[nbItems][nbItems];
		for (int i = 0; i < nbItems; i++) {
			for (int j = 0; j < nbItems; j++) {
				similaritiesMatrix[i][j] = userItemMatrix.getItemsSimilarity(i,
						j, similarity);
			}
		}
	}
}
