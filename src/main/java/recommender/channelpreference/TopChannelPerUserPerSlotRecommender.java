package recommender.channelpreference;

import static java.util.Comparator.comparing;
import static util.CurryingUtilities.curry1;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import model.Recommendation;
import scala.Tuple2;
import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

public class TopChannelPerUserPerSlotRecommender extends ChannelPreferenceRecommender{

	public TopChannelPerUserPerSlotRecommender(
			Context<RecsysTVProgram, RecsysTVEvent> context) {
		super(context, false, false);
	}
	
	@Override
	protected List<? extends Recommendation> recommendNormally(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		return tvPrograms.stream()
		.map(curry1(this::calculateTVProgramWatchTime, userId))
		.sorted(comparing(Tuple2<RecsysTVProgram, Integer>::_2).reversed())
		.limit(numberOfResults)
		.map(Tuple2<RecsysTVProgram, Integer>::_1)
		.map(Recommendation::new)
		.collect(Collectors.toList());
	}
	
	private Tuple2<RecsysTVProgram, Integer> calculateTVProgramWatchTime(int userId, RecsysTVProgram tvProgram){
		TreeSet<Tuple2<Integer, Integer>> currentTopChannelsWatchTime = topChannelsWatchTimePerSlotPerUser.get(userId).get(tvProgram.getSlot());
		int watchTime = 0;
		for(Tuple2<Integer, Integer> topChannelWatchTime : currentTopChannelsWatchTime){
			int sameChannelIndicator = topChannelWatchTime._1() == tvProgram.getChannelId() ? 1 : 0;
			watchTime = sameChannelIndicator * topChannelWatchTime._2();
		}
		return new Tuple2<RecsysTVProgram, Integer>(tvProgram, watchTime);
	}

	@Override
	protected List<? extends Recommendation> recommendForTesting(int userId,
			int numberOfResults, List<RecsysTVProgram> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
