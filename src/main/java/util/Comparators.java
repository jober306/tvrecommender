package util;

import java.io.Serializable;
import java.util.Comparator;

import model.recommendation.ScoredRecommendation;
import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensorCollection;

public class Comparators implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Comparator<UserPreference> UserPreferenceTensorComparator(UserPreferenceTensorCollection tensors){
		return new Comparator<UserPreference>(){

			@Override
			public int compare(UserPreference userPref1, UserPreference userPref2) {
				int watchTime1 = tensors.getUserPreferenceTensorWatchTime(userPref1);
				int watchTime2 = tensors.getUserPreferenceTensorWatchTime(userPref2);
				return Integer.compare(watchTime1, watchTime2);
			}
			
		};
	}
	
	public static Comparator<ScoredRecommendation> scoredRecommendationComparator(){
		return new Comparator<ScoredRecommendation>(){

			@Override
			public int compare(ScoredRecommendation program1, ScoredRecommendation program2) {
				return Double.compare(program2.score(), program1.score());
			}
		};
	}
}
