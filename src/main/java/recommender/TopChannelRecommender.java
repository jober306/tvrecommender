package recommender;

import static model.tensor.UserPreferenceTensorCollection.ANY;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.mllib.linalg.Vectors;

import model.Recommendation;
import model.tensor.UserPreference;
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
		extends AbstractTVRecommender<T, U> {

	/**
	 * The user preference tensor calculator used to create the tensors.
	 */
	UserPreferenceTensorCalculator<T, U> tensorCalculator;

	/**
	 * The top programs array per top channel. This is used by the recommender
	 * for testing because it caches all the top programs.
	 */
	Map<Integer, List<Recommendation>> recommendationsPerTopChannelIds;

	/**
	 * The top channel id for this data set. I.e. the channel with most watching
	 * time.
	 */
	int[] topChannelIds;

	public TopChannelRecommender(Context<T, U> context,
			UserPreferenceTensorCalculator<T, U> tensorCalculator) {
		super(context);
		this.tensorCalculator = tensorCalculator;
	}

	public void train() {
		calculateTopChannels();
		if (context instanceof EvaluationContext) {
			createTopProgramsPerChannelIds();
		}
	}

	@Override
	protected List<Recommendation> recommendNormally(int userId, int numberOfResults,
			List<T> tvProrams) {
		// The linked hash set is used to here to preserve order and to have
		// only distinct programs ids.
		Set<Recommendation> recommendations = new LinkedHashSet<Recommendation>();
		int currentChannelIndex = 0;
		while (recommendations.size() < numberOfResults && currentChannelIndex < topChannelIds.length) {
			int channelIndex = topChannelIds[currentChannelIndex];
			List<Recommendation> recommendationsForChannel = tvProrams.stream().filter(program -> program.getChannelId() == channelIndex).map(Recommendation::new).collect(Collectors.toList());
			recommendations.addAll(recommendationsForChannel);
			currentChannelIndex++;
		}
		return new ArrayList<Recommendation>(recommendations).subList(0,Math.min(recommendations.size(), numberOfResults));
	}

	@Override
	protected List<Recommendation> recommendForTesting(int userId,
			int numberOfResults, List<T> tvPrograms) {
		Set<Recommendation> recommendations = new LinkedHashSet<Recommendation>();
		int currentChannelIndex = 0;
		while (recommendations.size() < numberOfResults && currentChannelIndex < topChannelIds.length) {
			int topChannelId = topChannelIds[currentChannelIndex];
			recommendations.addAll(recommendationsPerTopChannelIds.get(topChannelId));
			currentChannelIndex++;
		}
		return new ArrayList<Recommendation>(recommendations).subList(0, Math.min(recommendations.size(), numberOfResults));
	}

	private void calculateTopChannels() {
		UserPreferenceTensorCollection tensorCollection = tensorCalculator
				.calculateUserPreferenceTensorForDataSet(
						context.getTrainingSet(),
						new ChannelFeatureExtractor<T, U>());
		List<Integer> unsortedChannelIds = context.getTrainingSet()
				.getAllChannelIds();
		topChannelIds = unsortedChannelIds.stream().map(channelId -> new UserPreference(ANY, Vectors.dense(new double[]{channelId}), ANY))
				.sorted(Comparators.UserPreferenceTensorComparator(tensorCollection))
				.mapToInt(userPref -> (int) userPref.programFeatureVector().apply(0)).toArray();
	}

	private void createTopProgramsPerChannelIds() {
		initializeProgramIdsPerTopChannelIds();
		EvaluationContext<T, U> evalContext = (EvaluationContext<T, U>) context;
		List<T> programsDuringTest = evalContext.getTestPrograms();
		programsDuringTest.stream().map(Recommendation::new).forEach(this::addRecommendation);
	}

	private void initializeProgramIdsPerTopChannelIds() {
		this.recommendationsPerTopChannelIds = new HashMap<Integer, List<Recommendation>>();
		for (int i = 0; i < topChannelIds.length; i++) {
			recommendationsPerTopChannelIds.put(topChannelIds[i],
					new ArrayList<Recommendation>());
		}
	}

	private void addRecommendation(Recommendation recommendation) {
		if (recommendationsPerTopChannelIds.containsKey(recommendation.getProgram().getChannelId())) {
			recommendationsPerTopChannelIds.get(recommendation.getProgram().getChannelId()).add(recommendation);
		}
	}
}
