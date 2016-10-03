package recommender.prediction;

import java.util.List;

import recommender.model.ItemSimilaritiesMatrix;
import recommender.model.UserItemMatrix;
import recommender.similarities.Similarity;

public class ItemBasedPredictor implements Predictor{
	
	ItemSimilaritiesMatrix model;
	Similarity similarity;
	
	int lastSeenItemIndex;
	
	public ItemBasedPredictor(UserItemMatrix U, Similarity similarity, int lastSeenItemIndex){
		this.similarity = similarity;
		this.lastSeenItemIndex = lastSeenItemIndex;
		model = new ItemSimilaritiesMatrix(U, this.similarity);
	}
	
	@Override
	public List<Integer> predict(int userID, int numberOfResults) {
		return model.getTopNSimilarRowIndices(lastSeenItemIndex, numberOfResults);
	}

}
