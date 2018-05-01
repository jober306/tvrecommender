package data;

import static java.util.stream.Collectors.toSet;
import static util.spark.mllib.MllibUtilities.sparseMatrixFormatToCSCMatrixFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.recommendation.Rating;

import com.google.common.collect.Sets;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.data.feature.FeatureExtractor;
import model.data.mapping.Mapping;
import model.information.Informative;
import model.matrix.DistributedUserTVProgramMatrix;
import model.matrix.LocalUserTVProgramMatrix;
import scala.Tuple2;
import scala.Tuple3;
import util.Lazy;
import util.function.SerializableSupplier;
import util.spark.SparkUtilities;
import util.time.LocalDateTimeDTO;

/**
 * Abstract class that represents a tv data set. It contains the tv events rdd
 * with its spark context and some utilities method on the RDD.
 * 
 * @author Jonathan Bergeron
 *
 * @param <U> The type of user contained in this data set.
 * @param <P> The type of tv pgoram contained in this data set.
 * @param <E> The type of tv event contained in this data set.
 */
public class TVDataSet<U extends User, P extends TVProgram, E extends TVEvent<U, P>> implements Serializable, Informative {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Utility methods to lazily load some fields expensive to compute. See the lazy interface for more information.
	 */
    static <U> SerializableSupplier<U> lazily(Lazy<U> lazy) { return lazy; }
	static <G> SerializableSupplier<G> value(G value) { return ()->value; }

	/**
	 * The rdd containing the data set.
	 */
	transient protected JavaRDD<E> eventsData;

	/**
	 * The java spark context used to load the data set.
	 */
	transient protected JavaSparkContext sc;
	
	
	/**
	 * All the lazy attributes. They are transient because Supplier is not Serializable, so each
	 * worker need to recalculate it.
	 */
	SerializableSupplier<Integer> numberOfUsers = lazily(() -> numberOfUsers = value(initNumberOfUsers()));
	SerializableSupplier<Integer> numberOfTvShows = lazily(() -> numberOfTvShows = value(initNumberOfTVShows()));
	SerializableSupplier<Integer> numberOfTvShowIndexes = lazily(() -> numberOfTvShowIndexes = value(initNumberOfTVShowIndexes()));
	SerializableSupplier<Long> numberOfTvEvents = lazily(() -> numberOfTvEvents = value(eventsData.count()));
	SerializableSupplier<Set<Integer>> allUserIds = lazily(() -> allUserIds = value(initAllUserIds()));
	SerializableSupplier<Set<U>> allUsers = lazily(() -> allUsers = value(initAllUsers()));
	SerializableSupplier<Set<Integer>> allProgramIds = lazily(() -> allProgramIds = value(initAllProgramIds()));
	SerializableSupplier<Set<P>> allPrograms = lazily(() -> allPrograms = value(initAllPrograms()));
	SerializableSupplier<Set<Integer>> allEventIds = lazily(() -> allEventIds = value(initAllEventIds()));
	SerializableSupplier<Set<Integer>> allChannelIds = lazily(() -> allChannelIds = value(initAllChannelIds()));
	SerializableSupplier<LocalDateTime> startTime = lazily(() -> startTime = value(initStartTime()));
	SerializableSupplier<LocalDateTime> endTime = lazily(() -> endTime = value(initEndTime()));	
	
	
	/**
	 * Abstract constructor that initialize the tv events data and the spark
	 * context. It will call the initialize method.
	 * 
	 * @param eventsData
	 * @param sc
	 */
	public TVDataSet(JavaRDD<E> eventsData) {
		this.eventsData = eventsData;
		this.sc = new JavaSparkContext(eventsData.context());
	}
	
	/**
	 * Method that cache the backing rdd containing this dataset.
	 */
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
	public boolean contains(E event) {
		JavaRDD<E> eventRDD = SparkUtilities.elementToJavaRDD(event, sc);
		JavaRDD<E> intersection = eventsData.intersection(eventRDD);
		return !intersection.isEmpty();
	}
	
	/**
	 * Method that finds all the tv program ids seen by a given user id.
	 * @param userId The user index
	 * @return The set of tv program ids seen by user having given user id.
	 */
	public Set<Integer> getTvProgramIndexesSeenByUser(int userId) {
		return getTVProgramSeenByUser(userId).stream()
				.map(P::programId)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Method that finds all the tv programs seen by a given user id.
	 * @param userId The user index
	 * @return The set of tv programs seen by user having given user id.
	 */
	public Set<P> getTVProgramSeenByUser(int userId){
		List<P> tvShowsSeenByUser = eventsData
				.filter(tvEvent -> tvEvent.getUserID() == userId)
				.map(E::getProgram)
				.collect();
		return Sets.newHashSet(tvShowsSeenByUser);
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
	public Set<TVDataSet<U, P, E>> splitTVEventsRandomly(double[] ratios) {
		JavaRDD<E>[] splittedEvents = eventsData.randomSplit(ratios);
		return Arrays.stream(splittedEvents).map(this::newInstance).collect(toSet());
	}
	
	/**
	 * Method that converts the data set into the good format for using mllib
	 * methods. A default score of 1 is given to each rating.
	 * 
	 * @return A java RDD of the <class>Rating</class> class.
	 */
	public JavaRDD<Rating> convertToMLlibRatings() {
		JavaRDD<Rating> ratings = eventsData.map(event -> new Rating(event
				.getUserID(), event.getProgram().programId(), 1.0));
		return ratings;
	}


	/**
	 * Method that transforms this data set into a distributed user-item like matrix.
	 * Each row and column are created with respect to the given user and tv program mapping.
	 * @param userMapping The user mapping.
	 * @param tvProgramMapping The tv program mapping
	 * @return The distributed user/tv program matrix representing this data set with respect to the given mappings.
	 */
	public <UM, PM> DistributedUserTVProgramMatrix<U, UM, P, PM> convertToDistUserItemMatrix(Mapping<U, UM> userMapping, Mapping<P, PM> tvProgramMapping) {
		final int numberOfTvShows = tvProgramMapping.size();
		Broadcast<Mapping<U, UM>> broadcastedUserMapping = sc.broadcast(userMapping);
		Broadcast<Mapping<P, PM>> broadcastedTVProgramMapping = sc.broadcast(tvProgramMapping);
		JavaRDD<IndexedRow> ratingMatrix = eventsData
				.mapToPair(event -> new Tuple2<Integer, Tuple2<Integer, Double>>(broadcastedUserMapping.value().valueToIndex(event.getUser()), new Tuple2<Integer, Double>(broadcastedTVProgramMapping.value().valueToIndex(event.getProgram()), 1.0d)))
				.aggregateByKey(new HashSet<Tuple2<Integer, Double>>(), (set, ele) -> {set.add(ele); return set;}, (set1, set2) -> {set1.addAll(set2); return set1;})
				.map(sparseRowRepresenation -> new IndexedRow(sparseRowRepresenation._1(), Vectors.sparse(numberOfTvShows, sparseRowRepresenation._2())));
		broadcastedUserMapping.unpersist();
		broadcastedTVProgramMapping.unpersist();
		return new DistributedUserTVProgramMatrix<>(ratingMatrix, userMapping, tvProgramMapping);
	}	
	
	/**
	 * Method that transforms this data set into a local user-item like matrix.
	 * Each row and column are created with respect to the given user and tv program mapping.
	 * @param userMapping The user mapping.
	 * @param tvProgramMapping The tv program mapping
	 * @return The local user/tv program matrix representing this data set with respect to the given mappings.
	 */
	public <UM, PM> LocalUserTVProgramMatrix<U, UM, P, PM> convertToLocalUserItemMatrix(Mapping<U, UM> userMapping, Mapping<P, PM> tvProgramMapping){
		final int numberOfUsers = userMapping.size();
		final int numberOfTvShows = tvProgramMapping.size();
		Broadcast<Mapping<U, UM>> broadcastedUserMapping = sc.broadcast(userMapping);
		Broadcast<Mapping<P, PM>> broadcastedTVProgramMapping = sc.broadcast(tvProgramMapping);
		List<MatrixEntry> tvShowIdUserIdEvent = eventsData.map(tvEvent -> new MatrixEntry(broadcastedUserMapping.value().valueToIndex(tvEvent.getUser()), broadcastedTVProgramMapping.value().valueToIndex(tvEvent.getProgram()), 1.0d)).distinct().collect();
		broadcastedUserMapping.unpersist();
		broadcastedTVProgramMapping.unpersist();
		Tuple3<int[], int[], double[]> matrixData = sparseMatrixFormatToCSCMatrixFormat(numberOfTvShows, tvShowIdUserIdEvent);
		return new LocalUserTVProgramMatrix<>(numberOfUsers, numberOfTvShows, matrixData._1(), matrixData._2(), matrixData._3(), userMapping, tvProgramMapping);
	}

	/**
	 * Method that transforms all tv program found in this data set into a distributed feature matrix.
	 * Each row represents the feature extractor representation of a tv program with respect to the given mapping.
	 * @param extractor The feature extractor.
	 * @param tvProgramMapping The tv program mapping.
	 * @return An indexed row matrix representing the feature extractor representation of all tv programs with respect to the given mapping.
	 */
	public IndexedRowMatrix getContentMatrix(FeatureExtractor<? super P, ? super E> extractor, Mapping<P, ?> tvProgramMapping) {
		Broadcast<Mapping<P, ?>> broadcastedTVProgramMapping = sc.broadcast(tvProgramMapping);
		JavaRDD<IndexedRow> contentMatrix = eventsData
				.mapToPair(tvEvent -> new Tuple2<P, E>(tvEvent.getProgram(), tvEvent))
				.reduceByKey((tvEvent1, tvEvent2) -> tvEvent1)
				.map(pair -> {
					E event = pair._2();
					int programIndex = broadcastedTVProgramMapping.value().valueToIndex(event.getProgram());
					return new IndexedRow(programIndex, extractor.extractFeaturesFromEvent(event));
				});
		broadcastedTVProgramMapping.unpersist();
		return new IndexedRowMatrix(contentMatrix.rdd());
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
	public TVDataSet<U, P, E> newInstance(JavaRDD<E> eventsData) {
		try {
			TVDataSet<U, P, E> newTvDataSet = this.getClass().getDeclaredConstructor(JavaRDD.class).newInstance(eventsData);
			return newTvDataSet;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Getter method that return the data attached to this data set.
	 * 
	 * @return The java RDD containing all the recsys tv event.
	 */
	public JavaRDD<E> getEventsData() {
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
	
	/**
	 * Method that returns all the unique user ids contained in this data set.
	 * @return The set of user ids.
	 */
	public Set<Integer> getAllUserIds() {
		return allUserIds.get();
	}
	
	/**
	 * Method that returns all the unique users contained in this data set. 
	 * @return The set of users.
	 */
	public Set<U> getAllUsers(){
		return allUsers.get();
	}
	
	/**
	 * Method that returns all the unique tv program ids contained in this data set.
	 * @return The set of tv program ids.
	 */
	public Set<Integer> getAllProgramIds() {
		return Sets.newHashSet(eventsData.map(E::getProgramID).distinct().collect());
	}
	
	/**
	 * Method that returns all the unique tv programs contained in this data set.
	 * @return The set of tv programs.
	 */
	public Set<P> getAllPrograms(){
		return allPrograms.get();
	}

	/**
	 * Method that returns all the unique event ids contained in this data set.
	 * @return The set of event ids.
	 */
	public Set<Integer> getAllEventIds() {
		return allEventIds.get();
	}

	/**
	 * Method that returns all the unique channel ids contained in this data set.
	 * @return The set of channel ids.
	 */
	public Set<Integer> getAllChannelIds() {
		return allChannelIds.get();
	}
	
	/**
	 * Method that compute the number of distinct users contained in this data set.
	 * @return The number of users contained in this data set.
	 */
	public int getNumberOfUsers(){
		return numberOfUsers.get();
	}
	
	/**
	 * Method that compute the number of distinct tv programs contained in this data set.
	 * @return The number of tv programs contained in this data set.
	 */
	public int getNumberOfTvPrograms(){
		return numberOfTvShows.get();
	}
	
	/**
	 * Method that compute the number of distinct tv program ids contained in this data set.
	 * @return The number of tv program ids contained in this data set.
	 */
	public int getNumberOfTvShowIds(){
		return numberOfTvShowIndexes.get();
	}
	
	/**
	 * Method that compute the number of events contained in this data set.
	 * @return The number of events contained in this data set.
	 */
	public long numberOfTvEvents(){
		return numberOfTvEvents.get();
	}
	
	/**
	 * Method that finds the earliest time an event occurred.
	 * @return The earliest event time.
	 */
	public LocalDateTime startTime(){
		return startTime.get();
	}
	
	/**
	 * Method that finds the latest time an event occurred.
	 * @return The latest event time.
	 */
	public LocalDateTime endTime(){
		return endTime.get();
	}
	
	@Override
	public TVDataSetInfo info(){
		return new TVDataSetInfo(this.getClass().getSimpleName(), getNumberOfUsers(), getNumberOfTvPrograms(), numberOfTvEvents());
	}
	
	private Set<Integer> initAllUserIds(){
		return Sets.newHashSet(getAllUsers().stream().map(U::id).collect(Collectors.toList()));
	}
	
	private Set<U> initAllUsers(){
		return Sets.newHashSet(eventsData.map(E::getUser).distinct().collect());
	}
	
	private Set<Integer> initAllProgramIds(){
		return Sets.newHashSet(getAllPrograms().stream().map(P::programId).collect(Collectors.toList()));
	}
	
	private Set<P> initAllPrograms(){
		return Sets.newHashSet(eventsData.map(E::getProgram).distinct().collect());
	}
	
	private Set<Integer> initAllEventIds(){
		return Sets.newHashSet(eventsData.map(E::getEventID).distinct().collect());
	}
	
	private Set<Integer> initAllChannelIds(){
		return Sets.newHashSet(eventsData.map(E::getChannelId).distinct().collect());
	}
	
	private int initNumberOfUsers(){
		return (int)eventsData.map(E::getUserID).distinct().count();
	}

	private int initNumberOfTVShows(){
		return (int)eventsData.map(E::getProgram).distinct().count();
	}
	
	private int initNumberOfTVShowIndexes(){
		return (int) eventsData.map(E::getProgramID).distinct().count();
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
}
