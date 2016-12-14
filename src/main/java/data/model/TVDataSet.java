package data.model;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import mllib.model.DistributedUserItemMatrix;
import recommender.model.UserItemMatrix;

/**
 * Abstract class that represents a tv data set. It is  a RDD of TVEvent with a spark context and
 * some utilities method on the RDD.
 * @author Jonathan Bergeron
 *
 * @param <T> A child class of the abstract class TVEvent. The RDD will be of this class.
 */
public abstract class TVDataSet<T extends TVEvent> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	transient protected JavaRDD<T> eventsData;
	transient protected JavaSparkContext sc;
	
	//------Data set constructor------------------------
	public TVDataSet(JavaRDD<T> eventsData, JavaSparkContext sc){
		this.eventsData = eventsData;
		this.sc = sc;
	}
	
	abstract public  TVDataSet<T> buildDataSetFromRawData(JavaRDD<T> eventsData, JavaSparkContext sc);
	
	//------Recommender model convertion method----------
	abstract public UserItemMatrix convertToUserItemMatrix();
	
	//----------ML lib convertion methods----------------
	abstract public JavaRDD<Rating> convertToMLlibRatings();
	abstract public DistributedUserItemMatrix convertToDistUserItemMatrix();
	abstract public IndexedRowMatrix getContentMatrix();
	
	//--------General Utilities methods--------------------
	/**
	 * Getter method that return the data attached to this data set.
	 * 
	 * @return The java RDD containing all the recsys tv event.
	 */
	public JavaRDD<T> getEventsData(){
		return eventsData;
	}
	
	/**
	 * Method that returns the java spark context used to load this data set.
	 * @return The java spark context used to load this data set.
	 */
	public JavaSparkContext getJavaSparkContext(){
		return sc;
	}
	
	abstract public boolean isEmpty();
	abstract public boolean contains(T event);
	
	/**
	 * Method that return the list of all distinct user Ids in the data set.
	 * 
	 * @return A list of integer representing all the distinct user Ids.
	 */
	public List<Integer> getAllUserIds() {
		return eventsData.map(tvEvent -> tvEvent.getUserID()).distinct()
				.collect();
	}

	/**
	 * Method that return the list of all distinct program Ids in the data set.
	 * 
	 * @return A list of integer representing all the distinct program Ids.
	 */
	public List<Integer> getAllProgramIds() {
		return eventsData.map(tvEvent -> tvEvent.getProgramId()).distinct()
				.collect();
	}

	/**
	 * Method that return the list of all distinct event Ids in the data set.
	 * 
	 * @return A list of integer representing all the distinct event Ids.
	 */
	public List<Integer> getAllEventIds() {
		return eventsData.map(tvEvent -> tvEvent.getEventID()).distinct()
				.collect();
	}
	
	/**
	 * Method that return the list of all distinct channel ids in the data set.
	 * 
	 * @return A list of integer representing all the distinct channel ids.
	 */
	public List<Integer> getAllChannelIds(){
		return eventsData.map(tvEvent -> tvEvent.getChannelId()).distinct().collect();
	}
	
	abstract public int getNumberOfUsers();
	abstract public int getNumberOfItems();
	abstract public List<Integer> getProgramIndexesSeenByUser(int userIndex);
	abstract public int count();
	abstract public JavaRDD<T>[] splitTVEventsRandomly(double[] ratios);
}
