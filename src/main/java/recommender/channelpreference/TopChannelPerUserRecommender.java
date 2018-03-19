package recommender.channelpreference;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelPerUserRecommender extends ChannelPreferenceRecommender{

	public TopChannelPerUserRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, false, true);
	}
}
