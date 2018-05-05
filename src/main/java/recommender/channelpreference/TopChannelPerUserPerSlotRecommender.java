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
import model.data.User;
import model.recommendation.Recommendations;
import model.tensor.UserPreference;
import scala.Tuple2;
import scala.Tuple3;

public class TopChannelPerUserPerSlotRecommender extends ChannelPreferenceRecommender{
	
	public TopChannelPerUserPerSlotRecommender(int numberOfRecommendations) {
		super(numberOfRecommendations, false, false);
	}
	
	public TopChannelPerUserPerSlotRecommender(Context<User, RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations) {
		super(context, numberOfRecommendations, false, false);
	}
	
	@Override
	protected Recommendations<User, RecsysTVProgram> recommendNormally(User user, List<RecsysTVProgram> tvPrograms) {
		return recommendByAggregatingSlots(user, tvPrograms);
	}
	
	private Recommendations<User, RecsysTVProgram> recommendByAggregatingSlots(User user, List<RecsysTVProgram> tvPrograms) {
		Stream<Short> allPossibleSlots = tvPrograms.stream()
				.map(RecsysTVProgram::slot)
				.distinct();
		List<Short> allPossibleChannels = tvPrograms.stream()
				.map(RecsysTVProgram::channelId)
				.distinct()
				.collect(Collectors.toList());
		Map<Integer, Integer> channelsWatchTime = allPossibleSlots.flatMap(slot -> allPossibleChannels.stream().map(channel -> toUserPreferenceTuple(user.id(), channel, slot)))
				.map(UserPreference::new)
				.map(this::toChannelWatchTime)
				.collect(Collectors.toMap(Tuple2::_1, Tuple2::_2, Integer::sum));
		List<Tuple2<Integer, Integer>> sortedChannelsWatchTime = channelsWatchTime.entrySet().stream()
				.map(entry -> new Tuple2<Integer, Integer>(entry.getKey(), entry.getValue()))
				.sorted(comparing(Tuple2<Integer, Integer>::_2).reversed())
				.collect(Collectors.toList());
		List<RecsysTVProgram> recommendations = recommendTopChannelsWithRespectToWatchTime(sortedChannelsWatchTime, numberOfRecommendations, tvPrograms);
		return new Recommendations<>(user, recommendations);
	}
	
	private Tuple3<Integer, Vector, Short> toUserPreferenceTuple(int userId, short channelId, short slot) {
		return new Tuple3<Integer, Vector, Short>(userId, Vectors.dense(new double[] {channelId}), slot);
	}
	
	private Tuple2<Integer, Integer> toChannelWatchTime(UserPreference userPreference){
		int channel = (int)userPreference.programFeatureVector().apply(0);
		int watchTime = userPreferenceCollection.getUserPreferenceTensorWatchTime(userPreference);
		return new Tuple2<Integer, Integer>(channel, watchTime);
	}
}
