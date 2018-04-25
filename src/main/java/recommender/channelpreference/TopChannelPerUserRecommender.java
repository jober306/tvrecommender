package recommender.channelpreference;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.data.User;

public class TopChannelPerUserRecommender extends ChannelPreferenceRecommender{
	
	public TopChannelPerUserRecommender(int numberOfRecommendations) {
		super(numberOfRecommendations, false, true);
	}
	
	public TopChannelPerUserRecommender(Context<User, RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations) {
		super(context, numberOfRecommendations, false, true);
	}
}
