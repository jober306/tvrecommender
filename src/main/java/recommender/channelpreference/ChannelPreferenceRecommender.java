package recommender.channelpreference;

import static data.recsys.RecsysTVEvent.getAllPossibleSlots;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static model.tensor.UserPreferenceTensorCollection.ANY;
import static util.CurryingUtilities.curry1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import model.Recommendation;
import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensorCollection;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import recommender.AbstractTVRecommender;
import scala.Tuple2;
import util.ListUtilities;
import data.Context;
import data.feature.ChannelFeatureExtractor;
import data.feature.FeatureExtractor;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.tensor.RecsysUserPreferenceTensorCalculator;

public abstract class ChannelPreferenceRecommender extends AbstractTVRecommender<RecsysTVProgram, RecsysTVEvent> {
		
	protected Map<Integer, Map<Integer, TreeSet<Tuple2<Integer,Integer>>>> topChannelsWatchTimePerSlotPerUser;
	
	final boolean anyUsers;
	final boolean anySlots;
	
	public ChannelPreferenceRecommender(Context<RecsysTVProgram, RecsysTVEvent> context, boolean anyUsers, boolean anySlots) {
		super(context);
		this.anyUsers = anyUsers;
		this.anySlots = anySlots;
	}

	@Override
	public void train() {
		this.topChannelsWatchTimePerSlotPerUser = new HashMap<Integer, Map<Integer, TreeSet<Tuple2<Integer, Integer>>>>();
		UserPreferenceTensorCollection userPreferenceCollection = calculateUserPrefCollection();
		List<UserPreference> allUserPref = generateAllPossibleUserPreferences();
		allUserPref.stream().forEach(curry1(this::addUserPreferenceToModel, userPreferenceCollection));
	}
	
	private UserPreferenceTensorCollection calculateUserPrefCollection(){
		FeatureExtractor<RecsysTVProgram, RecsysTVEvent> channelExtractor = new ChannelFeatureExtractor<>();
		RecsysUserPreferenceTensorCalculator userPrefCalculator = new RecsysUserPreferenceTensorCalculator();
		return userPrefCalculator.calculateUserPreferenceTensorForDataSet(context.getTrainingSet(), channelExtractor);		
	}
	
	private List<UserPreference> generateAllPossibleUserPreferences(){
		List<Integer> allUserIds = anyUsers ? Arrays.asList(ANY): context.getTrainingSet().getAllUserIds();
		List<Integer> allChannelIds = context.getTrainingSet().getAllChannelIds();
		List<Vector> allChannelIdsAsVector = allChannelIds.stream().map(channelId -> Vectors.dense(new double[]{channelId})).collect(toList());
		List<Integer> allSlots = anySlots ? Arrays.asList(ANY) : getAllPossibleSlots();
		return ListUtilities.cartesianProduct(allUserIds, allChannelIdsAsVector, allSlots).stream().map(UserPreference::new).collect(toList());
	}
	
	private void addUserPreferenceToModel(UserPreferenceTensorCollection userPreferenceCollection, UserPreference userPreference){
		if(!topChannelsWatchTimePerSlotPerUser.containsKey(userPreference.userId())){
			topChannelsWatchTimePerSlotPerUser.put(userPreference.userId(), new HashMap<Integer, TreeSet<Tuple2<Integer,Integer>>>());
		}
		if(!topChannelsWatchTimePerSlotPerUser.get(userPreference.userId()).containsKey(userPreference.slot())){
			topChannelsWatchTimePerSlotPerUser.get(userPreference.userId()).put(userPreference.slot(), new TreeSet<Tuple2<Integer, Integer>>(comparing(Tuple2<Integer, Integer>::_2).reversed()));
		}
		int watchTime = userPreferenceCollection.getUserPreferenceTensorsWatchTime(userPreference);
		if(watchTime != 0){
			//The program feature vector contains only the channel id.
			int channelId = (int) userPreference.programFeatureVector().apply(0);
			Tuple2<Integer, Integer> channelWatchTime = new Tuple2<>(channelId, watchTime);
			topChannelsWatchTimePerSlotPerUser.get(userPreference.userId()).get(userPreference.slot()).add(channelWatchTime);
		}
	}

	@Override
	abstract protected List<? extends Recommendation> recommendNormally(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms);
	
	@Override
	abstract protected List<? extends Recommendation> recommendForTesting(int userId, int numberOfResults, List<RecsysTVProgram> tvPrograms);
	
	protected List<? extends Recommendation> recommendTopChannelsWithRespectToWatchTime(TreeSet<Tuple2<Integer, Integer>> topChannelsWatchTime, int numberOfResults, List<RecsysTVProgram> tvPrograms){
		List<Recommendation> recommendations = new ArrayList<Recommendation>();
		for(Tuple2<Integer, Integer> topChannelWatchTime : topChannelsWatchTime){
			int channelIndex = topChannelWatchTime._1();
			List<Recommendation> recommendationsForChannel = tvPrograms.stream().filter(program -> program.getChannelId() == channelIndex).map(Recommendation::new).collect(Collectors.toList());
			recommendations.addAll(recommendationsForChannel);
			if(recommendations.size() > numberOfResults) {
					break;
			}
		}
		return recommendations.subList(0, Math.min(recommendations.size(), numberOfResults));
	}
}
