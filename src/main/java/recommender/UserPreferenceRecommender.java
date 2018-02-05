package recommender;

import java.util.List;

import data.Context;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;
import data.feature.ChannelFeatureExtractor;
import data.feature.FeatureExtractor;
import model.Recommendation;
import model.tensor.UserPreferenceTensorCalculator;
import model.tensor.UserPreferenceTensorCollection;

public class UserPreferenceRecommender <T extends TVProgram, U extends TVEvent>
extends AbstractTVRecommender<T, U> {
	
	final UserPreferenceTensorCalculator<T, U> userPreferenceCalculator;
	
	UserPreferenceTensorCollection userPreferenceCollection;
	
	public UserPreferenceRecommender(Context<T, U> context,
			UserPreferenceTensorCalculator<T, U> userPreferenceCalculator) {
		super(context);
		this.userPreferenceCalculator = userPreferenceCalculator;
	}

	@Override
	public void train() {
		TVDataSet<U> trainingSet = context.getTrainingSet();
		FeatureExtractor<T, U> channelExtractor = new ChannelFeatureExtractor<>();
		this.userPreferenceCollection = userPreferenceCalculator.calculateUserPreferenceTensorForDataSet(trainingSet, channelExtractor);		
		
	}

	@Override
	List<? extends Recommendation> recommendNormally(int userId, int numberOfResults, List<T> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	List<? extends Recommendation> recommendForTesting(int userId, int numberOfResults, List<T> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
