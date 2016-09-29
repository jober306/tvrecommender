package recommender.aggregation;

import java.util.List;

import recommender.model.UserItemMatrix;
import recommender.model.UserSimilaritiesMatrix;

public class MeanCenteredFunction implements AggregationFunction{

	@Override
	public double aggregate(UserItemMatrix X, UserSimilaritiesMatrix U, int userIndex, int itemIndex, int topN) {
		List<Integer> topNSimilarUser = U.getTopNSimilarRowIndices(userIndex, topN);
		double total = 0.0d;
		double totalSimilarity = 0.0d;
		for(int i = 0; i < topN; i++){
			double similarity = U.getSimilarity(userIndex, topNSimilarUser.get(i));
			total += similarity * (X.getRating(topNSimilarUser.get(i), itemIndex)- getAverageRatingOfUser(X, topNSimilarUser.get(i)));
			totalSimilarity += Math.abs(similarity);
		}
		double k = 1.0d / totalSimilarity;
		return getAverageRatingOfUser(X, userIndex) + k * total;	
	}
	
	private double getAverageRatingOfUser(UserItemMatrix X, int userIndex){
		int ratedItem = 0;
		int totalRating = 0;
		for(int i = 0; i < X.getNumberOfItems(); i++){
			double rating = X.getRating(userIndex, i);
			if(rating != 0){
				ratedItem++;
				totalRating += rating;
			}
		}
		return (double)totalRating / (double) ratedItem;
	}
	
}
