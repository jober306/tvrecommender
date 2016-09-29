package recommender.aggregation;

import recommender.model.UserItemMatrix;
import recommender.model.UserSimilaritiesMatrix;

public interface AggregationFunction {
	
	public double aggregate(UserItemMatrix X, UserSimilaritiesMatrix U, int userIndex, int itemIndex, int topN);
}
