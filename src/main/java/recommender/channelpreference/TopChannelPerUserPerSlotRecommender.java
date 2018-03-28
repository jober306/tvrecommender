package recommender.channelpreference;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.Context;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import model.tensor.UserPreference;
import scala.Tuple2;
import scala.Tuple3;

public class TopChannelPerUserPerSlotRecommender extends ChannelPreferenceRecommender{
	
	public TopChannelPerUserPerSlotRecommender(Context<RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations) {
		super(context, numberOfRecommendations, false, false);
	}
	
	@Override
	protected Recommendations<Recommendation> recommendNormally(int userId, List<RecsysTVProgram> tvPrograms) {
		return recommendByAggregatingSlots(userId, tvPrograms);
	}
	
	private Recommendations<Recommendation> recommendByAggregatingSlots(int userId, List<RecsysTVProgram> tvPrograms) {
		Stream<Short> allPossibleSlots = tvPrograms.stream()
				.map(RecsysTVProgram::slot)
				.distinct();
		List<Integer> allPossibleChannels = tvPrograms.stream()
				.map(RecsysTVProgram::channelId)
				.distinct()
				.collect(Collectors.toList());
		Map<Integer, Integer> channelsWatchTime = allPossibleSlots.flatMap(slot -> allPossibleChannels.stream().map(channel -> toUserPreferenceTuple(userId, channel, slot)))
				.map(UserPreference::new)
				.map(this::toChannelWatchTime)
				.collect(Collectors.toMap(Tuple2::_1, Tuple2::_2, Integer::sum));
		List<Tuple2<Integer, Integer>> sortedChannelsWatchTime = channelsWatchTime.entrySet().stream()
				.map(entry -> new Tuple2<Integer, Integer>(entry.getKey(), entry.getValue()))
				.sorted(comparing(Tuple2<Integer, Integer>::_2).reversed())
				.collect(Collectors.toList());
		List<Recommendation> recommendations = recommendTopChannelsWithRespectToWatchTime(sortedChannelsWatchTime, numberOfRecommendations, tvPrograms);
		return new Recommendations<>(userId, recommendations);
	}
	
	private Tuple3<Integer, Vector, Short> toUserPreferenceTuple(int userId, int channelId, short slot) {
		return new Tuple3<Integer, Vector, Short>(userId, Vectors.dense(new double[] {channelId}), slot);
	}
	
	private Tuple2<Integer, Integer> toChannelWatchTime(UserPreference userPreference){
		int channel = (int)userPreference.programFeatureVector().apply(0);
		int watchTime = userPreferenceCollection.getUserPreferenceTensorWatchTime(userPreference);
		return new Tuple2<Integer, Integer>(channel, watchTime);
	}
}
