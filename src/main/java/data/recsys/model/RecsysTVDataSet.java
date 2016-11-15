package data.recsys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CollectionAccumulator;

import recommender.model.UserItemMatrix;
import scala.Tuple2;
import spark.utilities.SparkUtilities;
import data.model.DataSet;
import data.recsys.mapper.RecSysMapCreator;
import data.recsys.mapper.RecSysMapReader;
import mllib.model.DistributedUserItemMatrix;

/**
 * Class that represents a data set of recsys tv event. The class holds the
 * spark context in which it has been loaded and offer multiple functionalities
 * on the data set.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSet implements DataSet, Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * The data set in JavaRDD format.
	 */
	transient JavaRDD<RecsysTVEvent> eventsData;

	/**
	 * The spark context in which the data was loaded.
	 */
	transient JavaSparkContext sc;

	/**
	 * The map reader that maps userID of the recsysTVDataset to an unique id
	 * between 1 and #of users.
	 */
	transient RecSysMapReader idMap;
	
	/**
	 * The broadcasted map reader to be used when the map is used with spark actions.
	 */
	Broadcast<RecSysMapReader> broadcastedIdMap;

	/**
	 * Boolean to check if wether or not the map file have been erased.
	 */
	transient boolean mapClosed;

	/**
	 * Main constructor of the class. Use the
	 * <class>RecsysTVDataSetLoader</class> to get the RDD off a csv file.
	 * 
	 * @param eventsData
	 *            The data in spark RDD format.
	 * @param sc
	 *            The java spark context in which the data has been loaded.
	 */
	public RecsysTVDataSet(JavaRDD<RecsysTVEvent> eventsData,
			JavaSparkContext sc) {
		this.eventsData = eventsData;
		this.sc = sc;
		initializeMapReader();
		broadcastedIdMap = sc.broadcast(idMap);
		mapClosed = false;
	}

	/**
	 * Method that initializes the map reader.
	 */
	public void initializeMapReader() {
		RecSysMapCreator mapCreator = new RecSysMapCreator();
		mapCreator.createUserIDToIDMap(getAllUserIds());
		mapCreator.createProgramIDToIDMap(getAllProgramIds());
		mapCreator.createEventIDToIDMap(getAllEventIds());
		idMap = new RecSysMapReader(mapCreator.getFileNames());
	}

	/**
	 * Getter method that return the data attached to this data set.
	 * 
	 * @return The java RDD containing all the recsys tv event.
	 */
	public JavaRDD<RecsysTVEvent> getEventsData() {
		return eventsData;
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
	public boolean contains(RecsysTVEvent event) {
		JavaRDD<RecsysTVEvent> eventRDD = SparkUtilities
				.<RecsysTVEvent> elementToJavaRDD(event, sc);
		JavaRDD<RecsysTVEvent> intersection = eventsData.intersection(eventRDD);
		return !intersection.isEmpty();
	}

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
		return eventsData.map(tvEvent -> tvEvent.getProgramID()).distinct()
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
	 * Method that count all distinct user in the data set.
	 * 
	 * @return The number of distinct users.
	 */
	public int getNumberOfUsers() {
		return (int) eventsData.map(tvEvent -> tvEvent.getUserID()).distinct()
				.count();
	}

	/**
	 * Method that count all distinct items in the data set.
	 * 
	 * @return The number of distinct items.
	 */
	public int getNumberOfItems() {
		return (int) eventsData.map(tvEvent -> tvEvent.getProgramID())
				.distinct().count();
	}

	/**
	 * Method that count all the events in the data set. (Events are assumed to
	 * be distinct).
	 */
	public int getNumberOfEvents() {
		return (int) eventsData.count();
	}
	
	/**
	 * Method that return the size of the data set. It is the same as getNumberOfEvents.
	 */
	public int count(){
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
	public RecsysTVDataSet[] splitDataRandomly(double[] ratios) {
		JavaRDD<RecsysTVEvent>[] split = eventsData.randomSplit(ratios);
		RecsysTVDataSet[] splittedDataSet = new RecsysTVDataSet[split.length];
		for(int i = 0; i < split.length; i++){
			splittedDataSet[i] = new RecsysTVDataSet(split[i], sc);
		}
		return splittedDataSet;
	}

	/**
	 * Method that splits the data with respect to the given ratios. The data is
	 * not randomly separated.
	 * 
	 * @param ratios
	 *            The ratios corresponding to the subset size.
	 * @return A list containing all the subsets.
	 */
	public RecsysTVDataSet[] splitDataDistributed(double[] ratios) {
		RecsysTVDataSet[] splittedData = new RecsysTVDataSet[ratios.length];
		int[] indexes = getIndexesCorrespondingToRatios(ratios);
		for (int i = 1; i <= ratios.length; i++) {
			final int lowerLimit = indexes[i - 1];
			final int upperLimit = indexes[i];
			JavaRDD<RecsysTVEvent> splitData = eventsData
					.filter(tvEvent -> lowerLimit <= broadcastedIdMap.getValue().getEventIDtoIDMap()
							.get(tvEvent.getEventID())
							&& broadcastedIdMap.getValue().getEventIDtoIDMap().get(
									tvEvent.getEventID()) < upperLimit);
			splittedData[i - 1] = new RecsysTVDataSet(splitData, sc);
		}
		return splittedData;
	}

	/**
	 * Method that return the indexes at which the data need to be splitted.
	 * 
	 * @param ratios
	 *            The ratio in each subset
	 * @return The indexes of when to create a new subset of the partition.
	 */
	public int[] getIndexesCorrespondingToRatios(double[] ratios) {
		int[] indexes = new int[ratios.length + 1];
		indexes[0] = 0;
		int total = getNumberOfEvents();
		for (int i = 1; i < ratios.length; i++) {
			indexes[i] = indexes[i - 1]
					+ (int) Math.floor(ratios[i - 1] * total);
			if (i == ratios.length - 1)
				indexes[indexes.length - 1] = total;
		}
		return indexes;
	}
	
	/**
	 * Method that returns tv events that have at least been viewed minTimeView
	 * time.
	 * 
	 * @param minTimeView
	 *            The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least
	 *         minTimeView time.
	 */
	public RecsysTVDataSet filterByMinTimeView(int minTimeView) {
		return new RecsysTVDataSet(eventsData.filter(tvEvent -> tvEvent.getDuration() >= minTimeView), sc);
	}
	
	/**
	 * Method that filters out all the events that occurred before min week and after max week.
	 * @param minWeek The minimum week number.
	 * @param maxWeek The maximum week number.
	 * @return The filtered data set.
	 */
	public RecsysTVDataSet filterByIntervalOfWeek(int minWeek, int maxWeek){
		return new RecsysTVDataSet(eventsData.filter(tvEvent -> tvEvent.getWeek() >= minWeek && tvEvent.getWeek() <= maxWeek), sc);
	}
	
	/**
	 * Method that filters out all the events that occurred before min slot and after max slot.
	 * @param minSlot The minimum week number.
	 * @param maxSlot The maximum week number.
	 * @return The filtered data set.
	 */
	public RecsysTVDataSet filterByIntervalOfSlot(int minSlot, int maxSlot){
		return new RecsysTVDataSet(eventsData.filter(tvEvent -> tvEvent.getSlot() >= minSlot && tvEvent.getSlot() <= maxSlot), sc);
	}
	
	/**
	 * Method that filters out all the events that occurred before min day and after max day.
	 * The data set does not specify at what day the week start. Min day and Max day take values
	 * between 1 (the first day of the week) and 7 (the last day of the week).
	 * @param minDay The minimum day number.
	 * @param maxDay The maximum day number.
	 * @return The filtered data set.
	 */
	public RecsysTVDataSet filterByIntervalOfDay(int minDay, int maxDay){
		return filterByIntervalOfSlot((minDay-1)*24 + 1, (maxDay)*24);
	}

	/**
	 * Method that converts the data set into the good format for using mllib
	 * methods.
	 * 
	 * @return A java RDD of the <class>Rating</class> class.
	 */
	public JavaRDD<Rating> convertToMLlibRatings() {
		JavaRDD<Rating> ratings = eventsData.map(event -> new Rating(event
				.getUserID(), event.getProgramID(), 1.0));
		return ratings;
	}
	
	/**
	 * Method that converts the data set into a distributed user item matrix.
	 * @return return the user item matrix in a distributed form corresponding to this data set.
	 */
	public DistributedUserItemMatrix convertToDistUserItemMatrix(){
		final int numberOfTvShows = getNumberOfItems();
		JavaRDD<IndexedRow> ratingMatrix = eventsData.mapToPair(event -> new Tuple2<Integer, RecsysTVEvent>(broadcastedIdMap.getValue().getUserIDToIdMap().get(event.getUserID()), event))
		.aggregateByKey(new HashSet<Tuple2<Integer, Double>>(), (list, event) -> {list.add(new Tuple2<Integer,Double>(broadcastedIdMap.getValue().getProgramIDtoIDMap().get(event.getProgramID()), 1.0d)); return list;}, (list1,list2)->{list1.addAll(list2); return list1;})
		.map(sparseRowRepresenation -> new IndexedRow(sparseRowRepresenation._1(), Vectors.sparse(numberOfTvShows, sparseRowRepresenation._2())));
		return new DistributedUserItemMatrix(ratingMatrix);
	}
	
	/**
	 * Method that returns the content matrix of each tv show.
	 */
	public IndexedRowMatrix getContentMatrix(){
		JavaRDD<IndexedRow> contentMatrix = eventsData.mapToPair(tvEvent -> new Tuple2<Integer, RecsysTVEvent>(tvEvent.getProgramID(), tvEvent))
		.reduceByKey((tvEvent1,tvEvent2) -> tvEvent1).map( pair -> {		
			RecsysTVEvent event =  pair._2();
			int programIndex = broadcastedIdMap.value().getProgramIDtoIDMap().get(event.getProgramID());
			return new IndexedRow(programIndex, Vectors.dense(extractFeaturesFromShow(event)));
		});
		return new IndexedRowMatrix(contentMatrix.rdd());
	}
	
	private double[] extractFeaturesFromShow(RecsysTVEvent event){
		double[] features = new double[4];
		features[0] = event.getChannelID();
		features[1] = event.getSlot();
		features[2] = event.getGenreID();
		features[3] = event.getSubgenreID();
		return features;
	}

	public UserItemMatrix convertToUserItemMatrix() {
		UserItemMatrix X = new UserItemMatrix(getNumberOfUsers(),
				getNumberOfItems());
		eventsData.collect()
				.forEach(
						tvEvent -> X.setUserSeenItem(
								idMap.getUserIDToIdMap().get(
										tvEvent.getUserID()),
								idMap.getProgramIDtoIDMap().get(
										tvEvent.getProgramID())));
		return X;
	}

	public int getOriginalUserID(int mappedID) {
		return (int) MapUtils.invertMap(idMap.getUserIDToIdMap()).get(mappedID);
	}

	public int getOriginalProgramID(int mappedID) {
		return (int) MapUtils.invertMap(idMap.getProgramIDtoIDMap()).get(
				mappedID);
	}

	public int getOriginalEventID(int mappedID) {
		return (int) MapUtils.invertMap(idMap.getEventIDtoIDMap())
				.get(mappedID);
	}

	public int getMappedUserID(int userID) {
		return idMap.getUserIDToIdMap().get(userID);
	}

	public int getMappedProgramID(int programID) {
		return idMap.getProgramIDtoIDMap().get(programID);
	}

	public int getMappedEventID(int eventID) {
		return idMap.getEventIDtoIDMap().get(eventID);
	}

	/**
	 * Method that releases all resource attached to this dataset.
	 */
	public void close() {
		sc.close();
		if (!mapClosed) {
			idMap.close();
			mapClosed = true;
		}
		eventsData = null;
	}

	@Override
	public void finalize() {
		if (!mapClosed) {
			idMap.close();
		}
	}
}
