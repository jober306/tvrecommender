package recommender.channelpreference;

import static model.tensor.UserPreferenceTensorCollection.ANY;

import java.util.List;
import java.util.TreeSet;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.IRecommendation;
import scala.Tuple2;

public class TopChannelRecommender extends ChannelPreferenceRecommender{

	public TopChannelRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, true, true);
	}
	
	@Override
	protected List<? extends IRecommendation> recommendNormally(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		TreeSet<Tuple2<Integer, Integer>> topChannelsWatchTime = topChannelsWatchTimePerSlotPerUser.get(ANY).get((short) ANY);
		return recommendTopChannelsWithRespectToWatchTime(topChannelsWatchTime, numberOfResults, tvPrograms);
	}

	@Override
	protected List<? extends IRecommendation> recommendForTesting(int userId,
			int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
