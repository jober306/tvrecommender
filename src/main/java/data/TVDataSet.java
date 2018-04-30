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
import java.util.function.Supplier;
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
import util.spark.SparkUtilities;
import util.time.LocalDateTimeDTO;

/**
 * Abstract class that represents a tv data set. It contains the tv events rdd
 * with its spark context and some utilities method on the RDD.
 * 
 * @author Jonathan Bergeron
 *
 * @param <E>
 *            A child class of the abstract class TVEvent. The RDD will be of
 *            this class.
 */
public class TVDataSet<U extends User, P extends TVProgram, E extends TVEvent<U, P>> implements Serializable, Informative {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Method to load lazily load attributes. See the lazy interface for more information.
	 */
    static <U> Supplier<U> lazily(Lazy<U> lazy) { return lazy; }
	static <G> Supplier<G> value(G value) { return ()->value; }

	/**
	 * The java rdd containing all the tv events.
	 */
	transient protected JavaRDD<E> eventsData;

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
	transient Supplier<Integer> numberOfTvShowIndexes = lazily(() -> numberOfTvShowIndexes = value(initNumberOfTVShowIndexes()));
	transient Supplier<Long> numberOfTvEvents = lazily(() -> numberOfTvEvents = value(eventsData.count()));
	transient Supplier<Set<Integer>> allUserIds = lazily(() -> allUserIds = value(initAllUserIds()));
	transient Supplier<Set<U>> allUsers = lazily(() -> allUsers = value(initAllUsers()));
	transient Supplier<Set<Integer>> allProgramIds = lazily(() -> allProgramIds = value(initAllProgramIds()));
	transient Supplier<Set<P>> allPrograms = lazily(() -> allPrograms = value(initAllPrograms()));
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
	public TVDataSet(JavaRDD<E> eventsData) {
		this.eventsData = eventsData;
		this.sc = new JavaSparkContext(eventsData.context());
	}
	
	/**
	 * Method that converts the data set into the good format for using mllib
	 * methods.
	 * 
	 * @return A java RDD of the <class>Rating</class> class.
	 */
	public JavaRDD<Rating> convertToMLlibRatings() {
		JavaRDD<Rating> ratings = eventsData.map(event -> new Rating(event
				.getUserID(), event.getProgram().programId(), 1.0));
		return ratings;
	}

	/**
	 * Method that converts the data set into a distributed user item matrix.
	 * 
	 * @return return the user item matrix in a distributed form corresponding
	 *         to this data set.
	 */
	public <UM, PM> DistributedUserTVProgramMatrix<U, UM, P, PM> convertToDistUserItemMatrix(Mapping<U, UM> userMapping, Mapping<P, PM> tvProgramMapping) {
		final int numberOfTvShows = tvProgramMapping.size();
		Broadcast<Mapping<U, UM>> broadcastedUserMapping = sc.broadcast(userMapping);
		Broadcast<Mapping<P, PM>> broadcastedTVProgramMapping = sc.broadcast(tvProgramMapping);
		JavaRDD<IndexedRow> ratingMatrix = eventsData
				.mapToPair(
						event -> new Tuple2<Integer, E>(
								broadcastedUserMapping.value().valueToIndex(event.getUser()), event))
				.aggregateByKey(
						new HashSet<Tuple2<Integer, Double>>(),
						(list, event) -> {
							list.add(new Tuple2<Integer, Double>(
									broadcastedTVProgramMapping.value().valueToIndex(event.getProgram()), 1.0d));
							return list;
						}, (list1, list2) -> {
							list1.addAll(list2);
							return list1;
						})
				.map(sparseRowRepresenation -> new IndexedRow(
						sparseRowRepresenation._1(), Vectors.sparse(
								numberOfTvShows, sparseRowRepresenation._2())));
		broadcastedUserMapping.unpersist();
		broadcastedTVProgramMapping.unpersist();
		return new DistributedUserTVProgramMatrix<>(ratingMatrix, userMapping, tvProgramMapping);
	}
	
	
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

	public <PM> IndexedRowMatrix getContentMatrix(FeatureExtractor<? super P, ? super E> extractor, Mapping<P, PM> tvProgramMapping) {
		Broadcast<Mapping<P, PM>> broadcastedTVProgramMapping = sc.broadcast(tvProgramMapping);
		JavaRDD<IndexedRow> contentMatrix = eventsData
				.mapToPair(
						tvEvent -> new Tuple2<P, E>(tvEvent
								.getProgram(), tvEvent))
				.reduceByKey((tvEvent1, tvEvent2) -> tvEvent1)
				.map(pair -> {
					E event = pair._2();
					int programIndex = broadcastedTVProgramMapping.value().valueToIndex(event.getProgram());
					return new IndexedRow(programIndex, extractor
							.extractFeaturesFromEvent(event));
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
	
	public Set<Integer> getTvProgramIndexesSeenByUser(int userIndex) {
		return Sets.newHashSet(eventsData
				.filter(tvEvent -> tvEvent.getUserID() == userIndex)
				.map(E::getProgram)
				.map(P::programId)
				.collect());
	}
	
	public Set<P> getTVProgramSeenByUser(int userIndex){
		List<P> tvShowsSeenByUser = eventsData
				.filter(tvEvent -> tvEvent.getUserID() == userIndex)
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
	
	public Set<Integer> getAllUserIds() {
		return allUserIds.get();
	}
	
	public Set<U> getAllUsers(){
		return allUsers.get();
	}
	
	public Set<U> initAllUsers(){
		return Sets.newHashSet(eventsData.map(E::getUser).distinct().collect());
	}
	
	public Set<Integer> getAllProgramIds() {
		return Sets.newHashSet(eventsData.map(E::getProgramID).distinct().collect());
	}
	
	public Set<P> getAllPrograms(){
		return allPrograms.get();
	}

	public Set<Integer> getAllEventIds() {
		return allEventIds.get();
	}
	
	public Set<Integer> initAllEventIds(){
		return Sets.newHashSet(eventsData.map(E::getEventID).distinct().collect());
	}

	public Set<Integer> getAllChannelIds() {
		return allChannelIds.get();
	}
	
	public Set<Integer> initAllChannelIds(){
		return Sets.newHashSet(eventsData.map(E::getChannelId).distinct().collect());
	}
	
	public int getNumberOfUsers(){
		return numberOfUsers.get();
	}
	
	public int getNumberOfTvShows(){
		return numberOfTvShows.get();
	}
	
	public int getNumberOfTvShowIndexes(){
		return numberOfTvShowIndexes.get();
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
	
	private Set<Integer> initAllUserIds(){
		return Sets.newHashSet(getAllUsers().stream().map(U::id).collect(Collectors.toList()));
	}
	
	private Set<Integer> initAllProgramIds(){
		return Sets.newHashSet(getAllPrograms().stream().map(P::programId).collect(Collectors.toList()));
	}
	
	private Set<P> initAllPrograms(){
		return Sets.newHashSet(eventsData.map(E::getProgram).distinct().collect());
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
