package recommender.channelpreference;

import static model.tensor.UserPreferenceTensorCollection.ANY;

import java.util.List;
import java.util.TreeSet;

import model.Recommendation;
import scala.Tuple2;
import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelPerUserRecommender extends ChannelPreferenceRecommender{

	public TopChannelPerUserRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, false, true);
	}
	
	@Override
	protected List<? extends Recommendation> recommendNormally(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		TreeSet<Tuple2<Integer, Integer>> topChannelsWatchTime = topChannelsWatchTimePerSlotPerUser.get(userId).get(ANY);
		return recommendTopChannelsWithRespectToWatchTime(topChannelsWatchTime, numberOfResults, tvPrograms);
	}

	@Override
	protected List<? extends Recommendation> recommendForTesting(int userId,
			int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}

}
