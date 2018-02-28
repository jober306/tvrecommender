package recommender.channelpreference;

import static model.tensor.UserPreferenceTensorCollection.ANY;

import java.util.List;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.IRecommendation;
import scala.Tuple2;

public class TopChannelPerUserRecommender extends ChannelPreferenceRecommender{

	public TopChannelPerUserRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, false, true);
	}
	
	@Override
	protected List<? extends IRecommendation> recommendNormally(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		if(!topChannelsWatchTimePerSlotPerUser.containsKey(userId)){
			return Lists.newArrayList();
		}
		TreeSet<Tuple2<Integer, Integer>> topChannelsWatchTime = topChannelsWatchTimePerSlotPerUser.get(userId).get((short) ANY);
		return recommendTopChannelsWithRespectToWatchTime(topChannelsWatchTime, numberOfResults, tvPrograms);
	}

	@Override
	protected List<? extends IRecommendation> recommendForTesting(int userId,
			int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		return recommendNormally(userId, numberOfResults, tvPrograms);
	}
}
