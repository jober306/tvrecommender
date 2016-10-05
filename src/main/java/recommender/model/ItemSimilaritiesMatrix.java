package recommender.model;

import recommender.model.linalg.SparseVector;
import recommender.similarities.CosineSimilarity;
import recommender.similarities.Similarity;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;

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
		similaritiesMatrix = new SparseVector[userItemMatrix.getNumberOfItems()];
		SparseVector[] sparseItems = userItemMatrix
				.getItemsInSparseVectorRepresentation();
		for (int i = 0; i < nbItems; i++) {
			double[] itemSimilarities = new double[userItemMatrix
					.getNumberOfItems()];
			for (int j = 0; j < nbItems; j++) {
				itemSimilarities[j] = similarity.calculateSimilarity(
						sparseItems[i], sparseItems[j]);
			}
			similaritiesMatrix[i] = new SparseVector(itemSimilarities);
		}
	}

	public static void main(String[] args) {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataSet = loader.loadDataSet();
		ItemSimilaritiesMatrix I = new ItemSimilaritiesMatrix(
				dataSet.convertToUserItemMatrix(),
				CosineSimilarity.getInstance());
		System.out.println(I.getNumberOfCol());
		System.out.println(I.getNumberOfRow());
	}
}
