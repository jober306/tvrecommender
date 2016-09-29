package recommender.aggregation;

import java.util.List;

import recommender.model.UserItemMatrix;
import recommender.model.UserSimilaritiesMatrix;

public class MeanFunction implements AggregationFunction{

	@Override
	public double aggregate(UserItemMatrix X, UserSimilaritiesMatrix U, int userIndex, int itemIndex, int topN) {
		List<Integer> topNSimilarUser = U.getTopNSimilarRowIndices(userIndex, topN);
		double total = 0.0d;
		for(int i = 0; i < topN; i++){
			total += X.getRating(topNSimilarUser.get(i), itemIndex);
		}
		return total / (double) topN;
	}

}
