package data;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import model.DistributedUserItemMatrix;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import data.feature.FeatureExtractor;

/**
 * Abstract class that represents a tv data set. It contains the tv events rdd
 * with its spark context and some utilities method on the RDD.
 * 
 * @author Jonathan Bergeron
 *
 * @param <T>
 *            A child class of the abstract class TVEvent. The RDD will be of
 *            this class.
 */
public abstract class TVDataSet<T extends TVEvent> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The java rdd containing all the tv events.
	 */
	transient protected JavaRDD<T> eventsData;

	/**
	 * The java spark context used to load the tv events.
	 */
	transient protected JavaSparkContext sc;

	/**
	 * Abstract constructor that initialize the tv events data and the spark
	 * context.
	 * 
	 * @param eventsData
	 * @param sc
	 */
	public TVDataSet(JavaRDD<T> eventsData, JavaSparkContext sc) {
		this.eventsData = eventsData;
		this.sc = sc;
		initialize();
	}

	abstract protected void initialize();

	/**
	 * Method used to create a new data set from some events and a spark
	 * context.
	 * 
	 * @param eventsData
	 *            The events data
	 * @param sc
	 * @return
	 */
	public TVDataSet<T> newInstance(JavaRDD<T> eventsData, JavaSparkContext sc) {
		try {
			return this
					.getClass()
					.getDeclaredConstructor(JavaRDD.class,
							JavaSparkContext.class).newInstance(eventsData, sc);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ----------ML lib convertion methods----------------
	abstract public JavaRDD<Rating> convertToMLlibRatings();

	abstract public DistributedUserItemMatrix convertToDistUserItemMatrix();

	abstract public IndexedRowMatrix getContentMatrix(
			FeatureExtractor<?, T> extractor);

	// --------General Utilities methods--------------------
	/**
	 * Getter method that return the data attached to this data set.
	 * 
	 * @return The java RDD containing all the recsys tv event.
	 */
	public JavaRDD<T> getEventsData() {
		return eventsData;
	}

	/**
	 * Method that returns the java spark context used to load this data set.
	 * 
	 * @return The java spark context used to load this data set.
	 */
	public JavaSparkContext getJavaSparkContext() {
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
	public List<Integer> getAllChannelIds() {
		return eventsData.map(tvEvent -> tvEvent.getChannelId()).distinct()
				.collect();
	}

	abstract public int getNumberOfUsers();

	abstract public int getNumberOfItems();

	abstract public List<Integer> getProgramIndexesSeenByUser(int userIndex);

	abstract public int count();

	abstract public JavaRDD<T>[] splitTVEventsRandomly(double[] ratios);
}
