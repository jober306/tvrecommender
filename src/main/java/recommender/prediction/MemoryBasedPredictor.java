package recommender.prediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.util.Pair;

import algorithm.QuickSelect;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;
import recommender.aggregation.AggregationFunction;
import recommender.aggregation.MeanFunction;
import recommender.model.UserItemMatrix;
import recommender.model.similarityMatrix.UserSimilaritiesMatrix;
import recommender.similarities.CosineSimilarity;

public class MemoryBasedPredictor implements Predictor{

	
	UserItemMatrix model;
	HashMap<Integer, List<Integer>> alreadyRatedIndexesPerUser;
	AggregationFunction function;
	int topN;
	
	public MemoryBasedPredictor(UserItemMatrix currentMatrix, AggregationFunction function, int topN) {
		this.function = function;
		this.topN = topN;
		alreadyRatedIndexesPerUser = new HashMap<Integer, List<Integer>>();
		buildModel(currentMatrix);
	}
	
	private void buildModel(UserItemMatrix X){
		model = new UserItemMatrix(X.getNumberOfUsers(), X.getNumberOfItems());
		UserSimilaritiesMatrix sm = X.getUserSimilaritiesMatrix(new CosineSimilarity());
		for(int user = 0; user < X.getNumberOfUsers(); user++){
			for(int item = 0; item < X.getNumberOfItems(); item++){
				double rating = X.getRating(user, item);
				if(rating != 0){
					model.setUserItemValue(user, item, rating);
					if(alreadyRatedIndexesPerUser.get(user)!=null){
						alreadyRatedIndexesPerUser.get(user).add(item);
					}
					else{
						alreadyRatedIndexesPerUser.put(user, new ArrayList<Integer>(item));
					}
				}else{
					double value = function.aggregate(X, sm, user, item, topN);
					model.setUserItemValue(user, item, value);
				}
			}
		}
	}
	
	@Override
	public List<Integer> predict(int userID, int numberOfResults) {
		List<Integer> predictions = new ArrayList<Integer>();
		double[] userRatings = model.getUserValues(userID).clone();
		for(int index : alreadyRatedIndexesPerUser.get(userID)){
			userRatings[index] = Double.MIN_VALUE;
		}
		List<Pair<Integer,Double>> topN = QuickSelect.selectTopN(ArrayUtils.toObject(userRatings), numberOfResults);
		for(int i = 0; i < numberOfResults; i++){
			predictions.add(topN.get(i).getFirst());
		}
		return predictions;
	}
	
	public static void main(String[] args){
		RecsysTVDataSetLoader dataSetLoader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataSet = dataSetLoader.loadDataSet();
		MemoryBasedPredictor predictor = new MemoryBasedPredictor(dataSet.convertToUserItemMatrix(), new MeanFunction(), 50);
		List<Integer> predictedItems = predictor.predict(3, 10);
	}
}