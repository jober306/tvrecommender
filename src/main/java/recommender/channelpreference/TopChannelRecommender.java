package recommender.channelpreference;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.data.User;

public class TopChannelRecommender extends ChannelPreferenceRecommender{
	
	public TopChannelRecommender(int numberOfRecommendations) {
		super(numberOfRecommendations, true, true);
	}
	
	public TopChannelRecommender(Context<User, RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations) {
		super(context, numberOfRecommendations, true, true);
	}
}
