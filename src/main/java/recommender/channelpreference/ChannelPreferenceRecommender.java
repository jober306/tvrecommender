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
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.tensor.RecsysUserPreferenceTensorCalculator;
import model.data.User;
import model.data.feature.ChannelFeatureExtractor;
import model.recommendation.Recommendations;
import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensorCollection;
import recommender.TVRecommender;
import scala.Tuple2;
import scala.Tuple3;

public abstract class ChannelPreferenceRecommender extends TVRecommender<User, RecsysTVProgram, RecsysTVEvent> {
		
	protected UserPreferenceTensorCollection userPreferenceCollection;
	
	final boolean anyUsers;
	final boolean anySlots;
	
	long currentUserPref = 1;
	long totalUserPref;
	long startTime;
	
	public ChannelPreferenceRecommender(int numberOfRecommendations, boolean anyUsers, boolean anySlots) {
		super(numberOfRecommendations);
		this.anyUsers = anyUsers;
		this.anySlots = anySlots;
	}
	
	public ChannelPreferenceRecommender(Context<User, RecsysTVProgram, RecsysTVEvent> context, int numberOfRecommendations, boolean anyUsers, boolean anySlots) {
		super(context, numberOfRecommendations);
		this.anyUsers = anyUsers;
		this.anySlots = anySlots;
	}

	@Override
	public void train() {
		userPreferenceCollection = calculateUserPrefCollection();
	}
	
	private UserPreferenceTensorCollection calculateUserPrefCollection(){
		ChannelFeatureExtractor channelExtractor = ChannelFeatureExtractor.instance();
		RecsysUserPreferenceTensorCalculator userPrefCalculator = new RecsysUserPreferenceTensorCalculator();
		return userPrefCalculator.calculateUserPreferenceTensorForDataSet(context.getTrainingSet(), channelExtractor, anyUsers, false, anySlots);		
	}

	@Override
	protected Recommendations<User, RecsysTVProgram> recommendNormally(User user, List<RecsysTVProgram> tvPrograms){
		List<RecsysTVProgram> recommendations = tvPrograms.stream()
				.map(curry1(this::toProgramWatchTime, user))
				.sorted(comparing(Tuple2<RecsysTVProgram, Integer>::_2).reversed())
				.limit(numberOfRecommendations)
				.map(Tuple2<RecsysTVProgram, Integer>::_1)
				.collect(Collectors.toList());
		return new Recommendations<>(user, recommendations);
	}
	
	@Override
	public Map<String, String> additionalParameters(){
		return Collections.emptyMap();
	}
	
	protected Tuple2<RecsysTVProgram, Integer> toProgramWatchTime(User user, RecsysTVProgram tvProgram){
		UserPreference userPreference = new UserPreference(toUserPreferenceTuple(user, tvProgram));
		int watchTime = userPreferenceCollection.getUserPreferenceTensorWatchTime(userPreference);
		return new Tuple2<RecsysTVProgram, Integer>(tvProgram, watchTime);
	}
	
	private Tuple3<Integer, Vector, Short> toUserPreferenceTuple(User user, RecsysTVProgram tvProgram){
		int userId = anyUsers ? ANY : user.id();
		short slot = anySlots ? (short) ANY : tvProgram.slot();
		Vector channel = Vectors.dense(new double[] {tvProgram.channelId()});
		return new Tuple3<Integer, Vector, Short>(userId, channel, slot);

	}
	
	protected List<RecsysTVProgram> recommendTopChannelsWithRespectToWatchTime(Collection<Tuple2<Integer, Integer>> topChannelsWatchTime, int numberOfResults, List<RecsysTVProgram> tvPrograms){
		List<RecsysTVProgram> recommendations = new ArrayList<RecsysTVProgram>();
		for(Tuple2<Integer, Integer> topChannelWatchTime : topChannelsWatchTime){
			int channelIndex = topChannelWatchTime._1();
			List<RecsysTVProgram> recommendationsForChannel = tvPrograms.stream().filter(program -> program.channelId() == channelIndex).collect(Collectors.toList());
			recommendations.addAll(recommendationsForChannel);
			if(recommendations.size() > numberOfResults) {
					break;
			}
		}
		//Wrapping the sublist into array list because sub list is not serializable.
		return new ArrayList<>(recommendations.subList(0, Math.min(recommendations.size(), numberOfResults)));
	}
	
	@Override
	protected Recommendations<User, RecsysTVProgram> recommendForTesting(User user, List<RecsysTVProgram> tvPrograms){
		return recommendNormally(user, tvPrograms);
	}
}
