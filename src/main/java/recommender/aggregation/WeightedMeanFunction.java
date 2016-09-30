package recommender.aggregation;

import java.util.List;

import recommender.model.UserItemMatrix;
import recommender.model.UserSimilaritiesMatrix;

public class WeightedMeanFunction implements AggregationFunction{
	
	@Override
	public double aggregate(UserItemMatrix X, UserSimilaritiesMatrix U, int userIndex, int itemIndex, int topN) {
		List<Integer> topNSimilarUser = U.getTopNSimilarRowIndices(userIndex, topN);
		double total = 0.0d;
		double totalSimilarity = 0.0d;
		for(int i = 0; i < topN; i++){
			double similarity = U.getSimilarityValue(userIndex, topNSimilarUser.get(i));
			total += similarity * X.getRating(topNSimilarUser.get(i), itemIndex);
			totalSimilarity += Math.abs(similarity);
		}
		double k = 1.0d / totalSimilarity;
		return k * total;
	}
}
