package recommender.model;

import recommender.similarities.Similarity;

public class ItemSimilaritiesMatrix extends SimilarityMatrix {
	
	public ItemSimilaritiesMatrix(UserItemMatrix userItemMatrix, Similarity similarity){
		this.similarity = similarity;
		calculateItemSimilaritiesMatrix(userItemMatrix);
	}
	
	private void calculateItemSimilaritiesMatrix(UserItemMatrix userItemMatrix){
		int nbItems = userItemMatrix.getNumberOfItems();
		similaritiesMatrix = new double[nbItems][nbItems];
		for(int i = 0; i < nbItems; i++){
			for(int j = 0; j < nbItems; j++){
				similaritiesMatrix[i][j] = userItemMatrix.getItemsSimilarity(i, j, similarity);
			}
		}
	}
}
