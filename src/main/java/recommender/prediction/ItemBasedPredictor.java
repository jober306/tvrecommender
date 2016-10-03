package recommender.prediction;

import java.util.List;

import data.recsys.loader.RecsysTVDataSetLoader;
import recommender.model.ItemSimilaritiesMatrix;
import recommender.model.UserItemMatrix;
import recommender.similarities.CosineSimilarity;
import recommender.similarities.Similarity;

public class ItemBasedPredictor implements Predictor{
	
	ItemSimilaritiesMatrix model;
	Similarity similarity;
	
	public ItemBasedPredictor(UserItemMatrix U, Similarity similarity){
		this.similarity = similarity;
		model = new ItemSimilaritiesMatrix(U, this.similarity);
	}
	
	@Override
	public List<Integer> predict(int lastSeenItemId, int numberOfResults) {
		return model.getTopNSimilarRowIndices(lastSeenItemId, numberOfResults);
	}
	
	public static void main(String[] args){
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader();
		ItemBasedPredictor p = new ItemBasedPredictor(loader.loadDataSet().convertToUserItemMatrix(), CosineSimilarity.getInstance());
		p.predict(, );
	}

}
