package recommender.prediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.util.Pair;

import algorithm.QuickSelect;
import recommender.aggregation.AggregationFunction;
import recommender.model.UserItemMatrix;
import recommender.model.UserSimilaritiesMatrix;
import recommender.similarities.Similarity;
import scala.Tuple2;

/**
 * TODO: Not implemented correctly. Should implements the Recommender interface.
 * @author iva
 *
 */
public class UserBasedRecommender {

	
	UserItemMatrix model;
	HashMap<Integer, List<Integer>> alreadyRatedIndexesPerUser;
	Similarity similarity;
	AggregationFunction function;
	int topN;
	
	public UserBasedRecommender(UserItemMatrix currentMatrix, Similarity similarity, AggregationFunction function, int topN) {
		this.function = function;
		this.topN = topN;
		this.similarity = similarity;
		alreadyRatedIndexesPerUser = currentMatrix.getItemIndexesSeenByUsers();
		model = currentMatrix;
		buildModel();
	}
	
	private void buildModel(){
		UserSimilaritiesMatrix sm = model.getUserSimilaritiesMatrix(similarity);
		for(int user = 0; user < model.getNumberOfUsers(); user++){
			for(int item = 0; item < model.getNumberOfItems(); item++){
				if(!alreadyRatedIndexesPerUser.get(user).contains(item)){
					double value = function.aggregate(model, sm, user, item, topN);
					model.setUserItemValue(user, item, value);
				}
			}
		}
	}
	
	public List<Integer> predict(int userID, int numberOfResults) {
		List<Integer> predictions = new ArrayList<Integer>();
		double[] userRatings = model.getUserValues(userID).clone();
		for(int index : alreadyRatedIndexesPerUser.get(userID)){
			userRatings[index] = Double.MIN_VALUE;
		}
		List<Tuple2<Integer,Double>> topN = QuickSelect.selectTopN(ArrayUtils.toObject(userRatings), numberOfResults);
		for(int i = 0; i < numberOfResults; i++){
			predictions.add(topN.get(i)._1());
		}
		return predictions;
	}
}
