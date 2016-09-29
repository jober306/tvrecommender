package recommender.model;

import recommender.similarities.Similarity;

public class UserSimilaritiesMatrix extends SimilarityMatrix{
	
	public UserSimilaritiesMatrix(UserItemMatrix userItemMatrix, Similarity similarity){
		this.similarity = similarity;
		calculateUserSimilaritiesMatrix(userItemMatrix);
	}
	
	private void calculateUserSimilaritiesMatrix(UserItemMatrix userItemMatrix){
		int nbUsers = userItemMatrix.getNumberOfUsers();
		similaritiesMatrix = new double[nbUsers][nbUsers];
		for(int i = 0; i < nbUsers; i++){
			for(int j = 0; j < nbUsers; j++){
				similaritiesMatrix[i][j] = userItemMatrix.getUsersSimilarity(i, j, similarity);
			}
		}
	}
}
