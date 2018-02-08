package recommender.channelpreference;

import static model.tensor.UserPreferenceTensorCollection.ANY;

import java.util.List;
import java.util.TreeSet;

import model.Recommendation;
import scala.Tuple2;
import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelRecommender extends ChannelPreferenceRecommender{

	public TopChannelRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, true, true);
	}
	
	@Override
	protected List<? extends Recommendation> recommendNormally(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		TreeSet<Tuple2<Integer, Integer>> topChannelsWatchTime = topChannelsWatchTimePerSlotPerUser.get(ANY).get(ANY);
		return recommendTopChannelsWithRespectToWatchTime(topChannelsWatchTime, numberOfResults, tvPrograms);
	}

	@Override
	protected List<? extends Recommendation> recommendForTesting(int userId,
			int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
