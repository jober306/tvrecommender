package recommender.prediction;

import java.util.List;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import recommender.model.ItemSimilaritiesMatrix;
import recommender.model.UserItemMatrix;
import recommender.similarities.CosineSimilarity;
import recommender.similarities.Similarity;

/**
 * TODO: Not implemented correctly. Should implements the Recommender interface.
 * @author iva
 *
 */
public class ItemBasedRecommender {
	
	ItemSimilaritiesMatrix model;
	Similarity similarity;
	
	public ItemBasedRecommender(UserItemMatrix U, Similarity similarity){
		this.similarity = similarity;
		model = new ItemSimilaritiesMatrix(U, this.similarity);
	}

	public List<Integer> predict(int lastSeenItemId, int numberOfResults) {
		return model.getTopNSimilarRowIndices(lastSeenItemId, numberOfResults);
	}
	
	public static void main(String[] args){
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataSet = loader.loadDataSet()._2();
		double[] ratios = {0.01,0.99};
		RecsysTVDataSet trainingSet = dataSet.splitDataDistributed(ratios)[0];
		System.out.println(trainingSet.getNumberOfItems());
		ItemBasedRecommender p = new ItemBasedRecommender(trainingSet.convertToUserItemMatrix(), CosineSimilarity.getInstance());
		List<Integer> predictions = p.predict(10, 10);
		for(int i = 0; i < predictions.size(); i++){
			System.out.println(predictions.get(i));
		}
	}

}
