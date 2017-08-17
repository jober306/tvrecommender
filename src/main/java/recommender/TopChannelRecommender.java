package recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import model.tensor.UserPreferenceTensorCalculator;
import model.tensor.UserPreferenceTensorCollection;

import util.Comparators;
import data.Context;
import data.EvaluationContext;
import data.TVEvent;
import data.TVProgram;
import data.feature.ChannelFeatureExtractor;

/**
 * Class that recommends a program based on the most popular channel for all
 * user and slot times. It is mostly used as a baseline recommender.
 * 
 * @author Jonathan Bergeron
 *
 */
public class TopChannelRecommender<T extends TVProgram, U extends TVEvent>
		extends TVRecommender<T, U> {

	/**
	 * The user preference tensor calculator used to create the tensors.
	 */
	UserPreferenceTensorCalculator<T, U> tensorCalculator;
	
	/**
	 * The top programs array per top channel. This is used by the recommender for testing because
	 * it caches all the top programs.
	 */
	Map<Integer, List<Integer>> programIdsPerTopChannelIds;

	/**
	 * The top channel id for this data set. I.e. the channel with most watching
	 * time.
	 */
	int[] topChannelIds;

	public TopChannelRecommender(Context<T, U> context, UserPreferenceTensorCalculator<T, U> tensorCalculator) {
		super(context);
		this.tensorCalculator = tensorCalculator;
	}
	
	public void train(){
		calculateTopChannels();
		if(context instanceof EvaluationContext<?, ?>){
			createTopProgramsPerChannelIds();
		}
	}

	@Override
	protected List<Integer> recommendNormally(int userId, int numberOfResults, List<T> tvProrams) {
		//The linked hash set is used to here to preserve order and to have only distinct programs ids.
		Set<Integer> topChannelsPrograms = new LinkedHashSet<Integer>();
		int currentChannelIndex = 0;
		while(topChannelsPrograms.size() < numberOfResults && currentChannelIndex <  topChannelIds.length){
			int channelIndex = topChannelIds[currentChannelIndex];
			List<Integer> currentChannelPrograms = tvProrams.stream()
					.filter(program -> program.getChannelId() == channelIndex)
					.map(program -> program.getProgramId()).collect(Collectors.toList());
			topChannelsPrograms.addAll(currentChannelPrograms);
			currentChannelIndex++;
		}
		return new ArrayList<Integer>(topChannelsPrograms).subList(0, Math.min(topChannelsPrograms.size(),numberOfResults));
	}
	
	@Override
	protected List<Integer> recommendForTesting(int userId, int numberOfResults, List<T> tvPrograms){
		Set<Integer> recommendations = new LinkedHashSet<Integer>();
		int currentChannelIndex = 0;
		while(recommendations.size() < numberOfResults && currentChannelIndex < topChannelIds.length){
			int topChannelId = topChannelIds[currentChannelIndex];
			recommendations.addAll(programIdsPerTopChannelIds.get(topChannelId));
			currentChannelIndex++;
		}
		return new ArrayList<Integer>(recommendations).subList(0, Math.min(recommendations.size(),numberOfResults));
	}

	private void calculateTopChannels() {
		UserPreferenceTensorCollection tensorCollection = tensorCalculator.calculateUserPreferenceTensorForDataSet(context.getTrainingSet(), new ChannelFeatureExtractor<T, U>());
		List<Integer> unsortedChannelIds = context.getTrainingSet().getAllChannelIds();
		topChannelIds = unsortedChannelIds.stream().sorted(Comparators.ChannelTensorComparator(tensorCollection)).mapToInt(Integer::intValue).toArray();
	}
	
	private void createTopProgramsPerChannelIds(){
		initializeProgramIdsPerTopChannelIds();
		EvaluationContext<T, U> evalContext = (EvaluationContext<T, U>) context;
		List<T> programsDuringTest = evalContext.getTestPrograms();
		programsDuringTest.stream().forEach(this::addProgram);
	}
	
	private void initializeProgramIdsPerTopChannelIds(){
		this.programIdsPerTopChannelIds = new HashMap<Integer, List<Integer>>();
		for(int i = 0; i < topChannelIds.length; i++){
			programIdsPerTopChannelIds.put(topChannelIds[i], new ArrayList<Integer>());
		}
	}
	
	private void addProgram(T program){
		if(programIdsPerTopChannelIds.containsKey(program.getChannelId())){
			programIdsPerTopChannelIds.get(program.getChannelId()).add(program.getProgramId());
		}
	}
}
