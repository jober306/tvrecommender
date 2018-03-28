package recommender.channelpreference;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelPerUserRecommender extends ChannelPreferenceRecommender{

	public TopChannelPerUserRecommender(Context<RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations) {
		super(context, numberOfRecommendations, false, true);
	}
}
