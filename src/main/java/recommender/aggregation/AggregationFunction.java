package recommender.aggregation;

import recommender.model.UserItemMatrix;
import recommender.model.UserSimilaritiesMatrix;

/**
 * Interface that force class to implements the aggregate function	
 * @author Jonathan Bergeron
 *
 */
public interface AggregationFunction {
	
	public double aggregate(UserItemMatrix X, UserSimilaritiesMatrix U, int userIndex, int itemIndex, int topN);
}
