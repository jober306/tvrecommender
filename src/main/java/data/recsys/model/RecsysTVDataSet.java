package data.recsys.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import recommender.model.UserItemMatrix;
import spark.utilities.SparkUtilities;
import data.model.DataSet;
import data.recsys.mapper.RecSysMapCreator;
import data.recsys.mapper.RecSysMapReader;

/**
 * Class that represents a data set of recsys tv event. The class holds the
 * spark context in which it has been loaded and offer multiple functionalities
 * on the data set.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSet implements DataSet {

	/**
	 * The data set in JavaRDD format.
	 */
	JavaRDD<RecsysTVEvent> eventsData;

	/**
	 * The spark context in which the data was loaded.
	 */
	JavaSparkContext sc;

	/**
	 * The map reader that maps userID of the recsysTVDataset to an unique id
	 * between 1 and #of users.
	 */
	RecSysMapReader idMap;

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
	}

	/**
	 * Method that initializes the map reader. TODO: There will be conflict if
	 * multiple datasets.
	 */
	public void initializeMapReader() {
		RecSysMapCreator mapCreator = new RecSysMapCreator();
		mapCreator.createMaps(this);
		idMap = new RecSysMapReader(mapCreator);
	}

	/**
	 * Check if the data is empty
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
	 * Method that splits the data into the number of entries in ratio array. Each subset respect the ratio proportion.
	 * @param ratios The ratios corresponding to the subset size.
	 * @return A list containing all the subsets.
	 */
	public List<JavaRDD<Rating>> splitUserData(double[] ratios) {
		List<JavaRDD<Rating>> splittedData = new ArrayList<JavaRDD<Rating>>();
		int[] indexes = getIndexesCorrespondingToRatios(ratios);
		for (int i = 0; i < ratios.length; i++) {
			int tempLowerLimit = i == 0 ? 0 : indexes[i-1];
			final int lowerLimit = tempLowerLimit;
			final int upperLimit = indexes[i];
			JavaRDD<Rating> splitData = eventsData
					.filter(tvEvent -> lowerLimit <= idMap.mapEventIDtoID(tvEvent
							.getEventID()) && idMap.mapEventIDtoID(tvEvent
									.getEventID()) < upperLimit).map(
							event -> new Rating(idMap.mapUserIDtoID(event
									.getUserID()), idMap.mapProgramIDtoID(event
									.getProgramID()), 1.0));
			splittedData.add(splitData);
		}
		return splittedData;
	}

	/**
	 * Method that return the indexes at which the data need to be splitted.
	 * @param ratios The ratio in each subset
	 * @return The indexes of when to create a new subset of the partition.
	 */
	public int[] getIndexesCorrespondingToRatios(double[] ratios) {
		int[] indexes = new int[ratios.length];
		int total = getNumberOfEvents();
		for (int i = 0; i < ratios.length; i++) {
			if (i == 0)
				indexes[0] = (int) Math.floor(ratios[0] * total);
			if (i == ratios.length)
				indexes[indexes.length - 1] = total - 1;
			else
				indexes[i] = indexes[i - 1] + (int)Math.floor(ratios[i] * total);
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
		return eventsData.map(event -> new Rating(idMap.mapUserIDtoID(event
				.getUserID()), idMap.mapProgramIDtoID(event.getProgramID()),
				1.0));
	}

	/**
	 * Method that converts the data into an RDD of IndexedRow that can be used
	 * to do distributed calculus.
	 * 
	 * @return An IndexedRowMatrix representing the data.
	 */
	public IndexedRowMatrix convertToMLLibUserItemMatrix() {
		UserItemMatrix userItemMatrix = convertToUserItemMatrix();
		JavaRDD<IndexedRow> rows = sc.parallelize(userItemMatrix
				.getMatrixRowsAsVector());
		return new IndexedRowMatrix(rows.rdd());
	}

	public IndexedRowMatrix convertToMLLibItemUserMatrix() {
		UserItemMatrix userItemMatrix = convertToUserItemMatrix();
		JavaRDD<IndexedRow> rows = sc.parallelize(userItemMatrix
				.getMatrixColumnAsVector());
		return new IndexedRowMatrix(rows.rdd());
	}

	/**
	 * Method that returns a Coordinate Matrix containing the cosine
	 * similarities between items
	 * 
	 * @return A CoordinateMatrix containing the similarities.
	 */
	public CoordinateMatrix calculateItemsCosineSimilarities() {
		IndexedRowMatrix rowMatrix = convertToMLLibUserItemMatrix();
		return rowMatrix.columnSimilarities();
	}

	/**
	 * Method that returns a Coordinate Matrix containing the cosine
	 * similarities between users
	 * 
	 * @return A CoordinateMatrix containing the similarities.
	 */
	public CoordinateMatrix calculateUsersCosineSimilarities() {
		IndexedRowMatrix columnMatrix = convertToMLLibItemUserMatrix();
		return columnMatrix.columnSimilarities();
	}

	public UserItemMatrix convertToUserItemMatrix() {
		UserItemMatrix X = new UserItemMatrix(getNumberOfUsers(),
				getNumberOfItems());
		eventsData.foreach(tvEvent -> X.setUserSeenItem(
				idMap.mapUserIDtoID(tvEvent.getUserID()),
				idMap.mapProgramIDtoID(tvEvent.getProgramID())));
		return X;
	}

	/**
	 * Method that releases all resource attached to this dataset.
	 */
	public void close() {
		sc.close();
		idMap.close();
		eventsData = null;
	}
}
