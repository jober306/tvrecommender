package recommender.channelpreference;

import static java.util.Comparator.comparing;
import static model.tensor.UserPreferenceTensorCollection.ANY;
import static util.currying.CurryingUtilities.curry1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.Context;
import data.feature.ChannelFeatureExtractor;
import data.feature.FeatureExtractor;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.tensor.RecsysUserPreferenceTensorCalculator;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensorCollection;
import recommender.AbstractTVRecommender;
import scala.Tuple2;
import scala.Tuple3;

public abstract class ChannelPreferenceRecommender extends AbstractTVRecommender<RecsysTVProgram, RecsysTVEvent, Recommendation> {
		
	protected UserPreferenceTensorCollection userPreferenceCollection;
	
	final boolean anyUsers;
	final boolean anySlots;
	
	long currentUserPref = 1;
	long totalUserPref;
	long startTime;
	
	public ChannelPreferenceRecommender(Context<RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations, boolean anyUsers, boolean anySlots) {
		super(context, numberOfRecommendations);
		this.anyUsers = anyUsers;
		this.anySlots = anySlots;
	}

	@Override
	public void train() {
		userPreferenceCollection = calculateUserPrefCollection();
	}
	
	private UserPreferenceTensorCollection calculateUserPrefCollection(){
		FeatureExtractor<RecsysTVProgram, RecsysTVEvent> channelExtractor = new ChannelFeatureExtractor<>();
		RecsysUserPreferenceTensorCalculator userPrefCalculator = new RecsysUserPreferenceTensorCalculator();
		return userPrefCalculator.calculateUserPreferenceTensorForDataSet(context.getTrainingSet(), channelExtractor, anyUsers, false, anySlots);		
	}

	@Override
	protected Recommendations<Recommendation> recommendNormally(int userId, List<RecsysTVProgram> tvPrograms){
		List<Recommendation> recommendations = tvPrograms.stream()
				.map(curry1(this::toProgramWatchTime, userId))
				.sorted(comparing(Tuple2<RecsysTVProgram, Integer>::_2).reversed())
				.limit(numberOfRecommendations)
				.map(Tuple2<RecsysTVProgram, Integer>::_1)
				.map(Recommendation::new)
				.collect(Collectors.toList());
		return new Recommendations<>(userId, recommendations);
	}
	
	@Override
	public Map<String, String> parameters(){
		return Collections.emptyMap();
	}
	
	protected Tuple2<RecsysTVProgram, Integer> toProgramWatchTime(int userId, RecsysTVProgram tvProgram){
		UserPreference userPreference = new UserPreference(toUserPreferenceTuple(userId, tvProgram));
		int watchTime = userPreferenceCollection.getUserPreferenceTensorWatchTime(userPreference);
		return new Tuple2<RecsysTVProgram, Integer>(tvProgram, watchTime);
	}
	
	private Tuple3<Integer, Vector, Short> toUserPreferenceTuple(int userId, RecsysTVProgram tvProgram){
		userId = anyUsers ? ANY : userId;
		short slot = anySlots ? (short) ANY : tvProgram.slot();
		Vector channel = Vectors.dense(new double[] {tvProgram.channelId()});
		return new Tuple3<Integer, Vector, Short>(userId, channel, slot);

	}
	
	protected List<Recommendation> recommendTopChannelsWithRespectToWatchTime(Collection<Tuple2<Integer, Integer>> topChannelsWatchTime, int numberOfResults, List<RecsysTVProgram> tvPrograms){
		List<Recommendation> recommendations = new ArrayList<Recommendation>();
		for(Tuple2<Integer, Integer> topChannelWatchTime : topChannelsWatchTime){
			int channelIndex = topChannelWatchTime._1();
			List<Recommendation> recommendationsForChannel = tvPrograms.stream().filter(program -> program.channelId() == channelIndex).map(Recommendation::new).collect(Collectors.toList());
			recommendations.addAll(recommendationsForChannel);
			if(recommendations.size() > numberOfResults) {
					break;
			}
		}
		return recommendations.subList(0, Math.min(recommendations.size(), numberOfResults));
	}
	
	@Override
	protected Recommendations<Recommendation> recommendForTesting(int userId, List<RecsysTVProgram> tvPrograms){
		return recommendNormally(userId, tvPrograms);
	}
}
