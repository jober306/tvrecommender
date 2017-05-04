package mllib.recommender;

import static mllib.model.tensor.UserPreferenceTensorCollection.ANY;

import java.time.LocalDateTime;
import java.util.List;

import mllib.model.tensor.UserPreferenceTensorCalculator;
import mllib.model.tensor.UserPreferenceTensorCollection;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import scala.Tuple2;
import data.feature.ChannelFeatureExtractor;
import data.model.EPG;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

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
	 * The top channel id for this data set. I.e. the channel with most watching
	 * time.
	 */
	int topChannelId;

	public TopChannelRecommender(EPG<T> epg, TVDataSet<U> tvDataSet,
			UserPreferenceTensorCalculator<T, U> tensorCalculator) {
		super(epg, tvDataSet);
		this.tensorCalculator = tensorCalculator;
	}

	public TopChannelRecommender(EPG<T> epg, TVDataSet<U> tvDataSet,
			LocalDateTime trainingStartTime, LocalDateTime trainingEndTime,
			UserPreferenceTensorCalculator<T, U> tensorCalculator) {
		super(epg, tvDataSet, trainingStartTime, trainingEndTime);
		this.tensorCalculator = tensorCalculator;
	}

	/**
	 * Method that train the space alignment recommender using the whole data
	 * set.
	 */
	public void train() {
		calculateTopChannel();
	}

	/**
	 * Recommend a program based on the given watch time. It will only consider
	 * program occurring during this watch time.
	 * 
	 * @param user
	 *            Dummy Param that is there only to satisfy the abstract parent
	 *            method signature. Can be filleld with null.
	 * @param targetWatchTime
	 *            The target watch time.
	 * @param numberOfResults
	 *            Dummy Param that is there only to satisfy the abstract parent
	 *            method signature. Can be filleld with null.
	 * 
	 * @return The recommended tv program id.
	 */
	public List<Integer> recommend(int user, LocalDateTime targetWatchTime,
			int numberOfResults) {
		JavaRDD<T> programDuringTargetTime = epg
				.getJavaRDDProgramsAtWatchTime(targetWatchTime);
		return retrieveTopChannelProgram(programDuringTargetTime);
	}

	/**
	 * Recommend a program based on the given watch time. It will only consider
	 * program occurring during this watch time.
	 * 
	 * @param user
	 *            Dummy Param that is there only to satisfy the abstract parent
	 *            method signature. Can be filleld with null.
	 * @param startTargetTime
	 *            The start target watch time.
	 * @param endTargetTime
	 *            The end target watch time.
	 * @param numberOfResults
	 *            Dummy Param that is there only to satisfy the abstract parent
	 *            method signature. Can be filleld with null.
	 * 
	 * @return The recommended tv program id.
	 */
	public List<Integer> recommend(int userId, LocalDateTime startTargetTime,
			LocalDateTime endTargetTime, int numberOfResults) {
		JavaRDD<T> programBetweenTargetTimes = epg
				.getJavaRDDProgramsBetweenTimes(startTargetTime, endTargetTime);
		return retrieveTopChannelProgram(programBetweenTargetTimes);
	}

	private List<Integer> retrieveTopChannelProgram(
			JavaRDD<T> allProgramOccuring) {
		final int topChannelId = this.topChannelId;
		List<Integer> topChannelProgram = allProgramOccuring
				.filter(program -> program.getChannelId() == topChannelId)
				.map(program -> program.getProgramId()).collect();
		return topChannelProgram;
	}

	private void calculateTopChannel() {
		UserPreferenceTensorCollection tensors = tensorCalculator
				.calculateUserPreferenceTensorForDataSet(trainingSet,
						new ChannelFeatureExtractor<T, U>());
		List<Integer> channelIds = trainingSet.getAllChannelIds();
		topChannelId = channelIds
				.stream()
				.map(channelId -> new Tuple2<Integer, Integer>(channelId,
						tensors.getUserPreferenceTensorsWatchTime(ANY,
								getChannelAsVector(channelId), ANY)))
				.max((channel1WatchTime, channel2WatchTime) -> channel1WatchTime
						._2().compareTo(channel2WatchTime._2())).get()._1();
	}

	private Vector getChannelAsVector(int channelId) {
		return Vectors.dense(new double[] { channelId });
	}
}
