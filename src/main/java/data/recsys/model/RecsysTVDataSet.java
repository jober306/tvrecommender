package data.recsys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
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
	 * Method that returns tv events that have at least been viewed minTimeView
	 * time.
	 * 
	 * @param minTimeView
	 *            The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least
	 *         minTimeView time.
	 */
	public JavaRDD<RecsysTVEvent> filterByMinTimeView(int minTimeView) {
		return eventsData
				.filter(tvEvent -> tvEvent.getDuration() >= minTimeView);
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
	 * Randomly split data with respect to the given ratios. The tv events are
	 * shuffled before creating the folders.
	 * 
	 * @param ratios
	 *            The ratio of Tv events there should be in each folder.
	 * @return An array of RecsysTVDataSet.
	 */
	public RecsysTVDataSet[] splitDataRandomly(double[] ratios) {
		RecsysTVDataSet[] splittedData = new RecsysTVDataSet[ratios.length];
		int[] tvEventsInFolder = getIndexesCorrespondingToRatios(ratios);
		List<RecsysTVEvent> tvEvents = new ArrayList<RecsysTVEvent>(
				eventsData.collect());
		Collections.shuffle(tvEvents);
		for (int i = 1; i < tvEventsInFolder.length; i++) {
			List<RecsysTVEvent> folder = new ArrayList<RecsysTVEvent>();
			for (int j = tvEventsInFolder[i - 1]; j < tvEventsInFolder[i]; j++) {
				folder.add(tvEvents.get(j));
			}
			JavaRDD<RecsysTVEvent> rddTvEventFolder = SparkUtilities
					.<RecsysTVEvent> elementsToJavaRDD(folder, sc);
			splittedData[i - 1] = new RecsysTVDataSet(rddTvEventFolder, sc);
		}
		return splittedData;
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
	
	public DistributedUserItemMatrix convertToDistUserItemMatrix(){
		ArrayList<CollectionAccumulator<Tuple2<Integer,Double>>> indexedRowAccumulators = new ArrayList<CollectionAccumulator<Tuple2<Integer,Double>>>();
		for(int i = 0; i < getNumberOfUsers(); i++){
			indexedRowAccumulators.add(new CollectionAccumulator<Tuple2<Integer,Double>>());
		}
		eventsData.foreach(tvEvent -> {
			int mappedUserId = broadcastedIdMap.value().getUserIDToIdMap().get(tvEvent.getUserID());
			int mappedProgramId = broadcastedIdMap.value().getProgramIDtoIDMap().get(tvEvent.getProgramID());
			indexedRowAccumulators.get(mappedUserId).add(new Tuple2<Integer,Double>(mappedProgramId, 1.0d));}
		);
		ArrayList<IndexedRow> indexedRows = new ArrayList<IndexedRow>();
		int numberOfItems = getNumberOfItems();
		for(int row = 0; row < indexedRowAccumulators.size(); row++){
			indexedRows.add(new IndexedRow(row, Vectors.sparse(numberOfItems, indexedRowAccumulators.get(row).value())));
		}
		return new DistributedUserItemMatrix(SparkUtilities.elementsToJavaRDD(indexedRows, sc));
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
