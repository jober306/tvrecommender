package data;

import static java.util.stream.Collectors.toSet;
import static util.currying.CurryingUtilities.curry2;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import com.google.common.collect.Sets;

import data.feature.FeatureExtractor;
import evaluator.information.Informative;
import model.DistributedUserItemMatrix;
import model.LocalUserItemMatrix;
import util.Lazy;
import util.spark.SparkUtilities;
import util.time.LocalDateTimeDTO;

/**
 * Abstract class that represents a tv data set. It contains the tv events rdd
 * with its spark context and some utilities method on the RDD.
 * 
 * @author Jonathan Bergeron
 *
 * @param <U>
 *            A child class of the abstract class TVEvent. The RDD will be of
 *            this class.
 */
public abstract class TVDataSet<T extends TVProgram, U extends TVEvent> implements Serializable, Informative {
	
	// ----------ML lib convertion methods----------------
	abstract public JavaRDD<Rating> convertToMLlibRatings();

	abstract public DistributedUserItemMatrix convertToDistUserItemMatrix();
	
	abstract public LocalUserItemMatrix convertToLocalUserItemMatrix();

	abstract public IndexedRowMatrix getContentMatrix(FeatureExtractor<? extends TVProgram, U> extractor);
	
	private static final long serialVersionUID = 1L;
	/**
	 * Method to load lazily load attributes. See the lazy interface for more information.
	 */
    static <U> Supplier<U> lazily(Lazy<U> lazy) { return lazy; }
	static <G> Supplier<G> value(G value) { return ()->value; }

	/**
	 * The java rdd containing all the tv events.
	 */
	transient protected JavaRDD<U> eventsData;

	/**
	 * The java spark context used to load the tv events.
	 */
	transient protected JavaSparkContext sc;
	
	
	/**
	 * Attributes that hold the number of users and tv shows. They are lazy initialized
	 * when the getters are called because spark needs to do a collect on the whole data set which
	 * can be long.
	 */
	transient Supplier<Integer> numberOfUsers = lazily(() -> numberOfUsers = value(initNumberOfUsers()));
	transient Supplier<Integer> numberOfTvShows = lazily(() -> numberOfTvShows = value(initNumberOfTVShows()));
	transient Supplier<Long> numberOfTvEvents = lazily(() -> numberOfTvEvents = value(eventsData.count()));
	transient Supplier<Set<Integer>> allUserIds = lazily(() -> allUserIds = value(initAllUserIds()));
	transient Supplier<Set<Integer>> allProgramIds = lazily(() -> allProgramIds = value(initAllProgramIds()));
	transient Supplier<Set<Integer>> allEventIds = lazily(() -> allEventIds = value(initAllEventIds()));
	transient Supplier<Set<Integer>> allChannelIds = lazily(() -> allChannelIds = value(initAllChannelIds()));
	transient Supplier<LocalDateTime> startTime = lazily(() -> startTime = value(initStartTime()));
	transient Supplier<LocalDateTime> endTime = lazily(() -> endTime = value(initEndTime()));
	
	/**
	 * Abstract constructor that initialize the tv events data and the spark
	 * context. It will call the initialize method.
	 * 
	 * @param eventsData
	 * @param sc
	 */
	public TVDataSet(JavaRDD<U> eventsData, JavaSparkContext sc) {
		this.eventsData = eventsData;
		this.sc = sc;
	}

	/**
	 * Method used to create a new data set from some events and a spark
	 * context.
	 * 
	 * @param eventsData
	 *            The events data
	 * @param sc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TVDataSet<T, U> newInstance(JavaRDD<U> eventsData, JavaSparkContext sc) {
		try {
			TVDataSet<T, U> newTvDataSet = this.getClass().getDeclaredConstructor(JavaRDD.class, JavaSparkContext.class).newInstance(eventsData, sc);
			return newTvDataSet;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void cache(){
		eventsData = eventsData.cache();
	}

	/**
	 * Check if the data set is empty.
	 * 
	 * @return true if the data set is empty
	 */
	public boolean isEmpty() {
		return eventsData.isEmpty();
	}

	/**
	 * Method that check if a particular event is in the data set. The
	 * comparison are done by the <method>equals</method> and
	 * <method>hashCode</method> methods of the <class>RecsysTVEvent</class>.
	 * 
	 * @param event
	 *            The event to be tested if it is in the data set.
	 * @return True if the event is in the data set, false otherwise.
	 */
	public boolean contains(U event) {
		JavaRDD<U> eventRDD = SparkUtilities
				.<U> elementToJavaRDD(event, sc);
		JavaRDD<U> intersection = eventsData.intersection(eventRDD);
		return !intersection.isEmpty();
	}
	
	public Set<Integer> getTvProgramSeenByUser(int userIndex) {
		return Sets.newHashSet(eventsData.filter(tvEvent -> tvEvent.getUserID() == userIndex)
				.map(tvEvent -> tvEvent.getProgramId()).distinct().collect());
	}

	/**
	 * Method that return the size of the data set. It is the same as
	 * getNumberOfEvents.
	 */
	public int count() {
		return (int) eventsData.count();
	}

	/**
	 * Randomly split data with respect to the given ratios. The tv events are
	 * shuffled before creating the folders.
	 * 
	 * @param ratios
	 *            The ratio of Tv events there should be in each folder.
	 * @return An array of RecsysTVDataSet.
	 */
	public Set<TVDataSet<T, U>> splitTVEventsRandomly(double[] ratios) {
		JavaRDD<U>[] splittedEvents = eventsData.randomSplit(ratios);
		return Arrays.stream(splittedEvents).map(curry2(this::newInstance, sc)).collect(toSet());
	}

	/**
	 * Getter method that return the data attached to this data set.
	 * 
	 * @return The java RDD containing all the recsys tv event.
	 */
	public JavaRDD<U> getEventsData() {
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
	
	public Set<Integer> getAllUserIds() {
		return allUserIds.get();
	}
	
	private Set<Integer> initAllUserIds(){
		return Sets.newHashSet(eventsData.map(tvEvent -> tvEvent.getUserID()).distinct().collect());
	}

	public Set<Integer> getAllProgramIds() {
		return Sets.newHashSet(eventsData.map(tvEvent -> tvEvent.getProgramId()).distinct().collect());
	}
	
	private Set<Integer> initAllProgramIds(){
		return Sets.newHashSet(eventsData.map(tvEvent -> tvEvent.getProgramId()).distinct().collect());
	}

	public Set<Integer> getAllEventIds() {
		return allEventIds.get();
	}
	
	public Set<Integer> initAllEventIds(){
		return Sets.newHashSet(eventsData.map(tvEvent -> tvEvent.getEventID()).distinct().collect());
	}

	public Set<Integer> getAllChannelIds() {
		return allChannelIds.get();
	}
	
	public Set<Integer> initAllChannelIds(){
		return Sets.newHashSet(eventsData.map(tvEvent -> tvEvent.getChannelId()).distinct().collect());
	}

	private int initNumberOfUsers(){
		return (int)eventsData.map(tvEvent -> tvEvent.getUserID()).distinct().count();
	}

	private int initNumberOfTVShows(){
		return (int)eventsData.map(tvEvent -> tvEvent.getProgramId()).distinct().count();
	}
	
	private LocalDateTime initStartTime(){
		return eventsData
				.map(TVEvent::getWatchTime)
				.map(LocalDateTimeDTO::new)
				.reduce(LocalDateTimeDTO::min)
				.toLocalDateTime();
	}
	
	private LocalDateTime initEndTime(){
		return eventsData
				.map(TVEvent::getWatchTime)
				.map(LocalDateTimeDTO::new)
				.reduce(LocalDateTimeDTO::max)
				.toLocalDateTime();
	}
	
	public int getNumberOfUsers(){
		return numberOfUsers.get();
	}
	
	public int getNumberOfTvShows(){
		return numberOfTvShows.get();
	}
	
	public long numberOfTvEvents(){
		return numberOfTvEvents.get();
	}
	
	public LocalDateTime startTime(){
		return startTime.get();
	}
	
	public LocalDateTime endTime(){
		return endTime.get();
	}
	
	public TVDataSetInfo info(){
		return new TVDataSetInfo(this.getClass().getSimpleName(), getNumberOfUsers(), getNumberOfTvShows(), numberOfTvEvents());
	}
}
