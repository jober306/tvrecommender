package recommender.channelpreference;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelRecommender extends ChannelPreferenceRecommender{
	
	public TopChannelRecommender(int numberOfRecommendations) {
		super(numberOfRecommendations, true, true);
	}
	
	public TopChannelRecommender(Context<RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations) {
		super(context, numberOfRecommendations, true, true);
	}
}
