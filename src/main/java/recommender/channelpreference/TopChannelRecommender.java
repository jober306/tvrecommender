package recommender.channelpreference;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelRecommender extends ChannelPreferenceRecommender{

	public TopChannelRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, true, true);
	}
}
