package mllib.recommender;

import static mllib.model.tensor.UserPreferenceTensorCollection.*; 

import static data.utility.TVDataSetUtilities.*;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.feature.ChannelFeatureExtractor;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;
import data.model.recsys.model.tensor.RecsysUserPreferenceTensorCalculator;
import mllib.model.tensor.UserPreferenceTensorCalculator;
import mllib.model.tensor.UserPreferenceTensorCollection;
import scala.Tuple2;

/**
 * Class that recommends a program based on the most popular channel for all user and slot times.
 * It is mostly used as a baseline recommender.
 * @author Jonathan Bergeron
 *
 */
public class TopChannelRecommender <U extends TVProgram, T extends TVEvent>{
	
	/**
	 * The tv data set on which the matrix M prime will be build.
	 */
	TVDataSet<T> tvDataset;
	
	/**
	 * The user preference tensor calculator used to create the tensors.
	 */
	UserPreferenceTensorCalculator<U,T> tensorCalculator;
	
	/**
	 * The top channel id for this data set. I.e. the channel with most watching
	 * time.
	 */
	int topChannelId;
	
	public TopChannelRecommender(TVDataSet<T> tvDataSet, UserPreferenceTensorCalculator<U,T> tensorCalculator){
		this.tvDataset = tvDataSet;
		this.tensorCalculator = tensorCalculator;
		calculateTopChannel();
	}
	
	/**
	 * Recommend a program based on the given slot and week.
	 * @param week The week that the recommended program must be in.
	 * @param slot The slot that the recommended program must be in.
	 * @return The original id of the tv program.
	 */
	public int recommend(int week, int slot){
		final int topChannelId = this.topChannelId;
		JavaRDD<T> programDuringWeekSlot = filterByIntervalOfSlot(filterByIntervalOfWeek(tvDataset.getEventsData(), week, week), slot, slot);
		List<T> oh = programDuringWeekSlot.filter(tvEvent -> tvEvent.getChannelId() == topChannelId).collect();
		return oh.get(0).getProgramId();
	}
	
	private void calculateTopChannel(){
		UserPreferenceTensorCollection tensors = tensorCalculator.calculateUserPreferenceTensorForDataSet(tvDataset, new ChannelFeatureExtractor<U,T>());
		List<Integer> channelIds = tvDataset.getAllChannelIds();
		topChannelId = channelIds.stream().map(channelId -> 
			new Tuple2<Integer,Integer>(channelId, 
					tensors.getUserPreferenceTensorsWatchTime(ANY, getChannelAsVector(channelId), ANY)))
		.max((channel1WatchTime, channel2WatchTime) -> channel1WatchTime._2().compareTo(channel2WatchTime._2())).get()._1();
	}
	
	private Vector getChannelAsVector(int channelId){
		return Vectors.dense(new double[]{channelId});
	}
}
