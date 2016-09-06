package data.recsys.model;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import data.model.DataSet;
import data.recsys.mapper.RecSysMapReader;
import recommender.model.UserItemMatrix;
import scala.reflect.ClassTag;
import spark.utilities.SparkUtilities;

/**
 * Class that represents a data set of recsys tv event. The class holds the spark context in which it has been loaded and offer
 * multiple functionalities on the data set.
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSet implements DataSet{
	
	/**
	 * The data set in JavaRDD format.
	 */
	JavaRDD<RecsysTVEvent> eventsData;
	
	/**
	 * The spark context in which the data was loaded.
	 */
	JavaSparkContext sc;
	
	/**
	 * Main constructor of the class. Use the <class>RecsysTVDataSetLoader</class> to get the RDD off a csv file.
	 * @param eventsData The data in spark RDD format.
	 * @param sc The java spark context in which the data has been loaded.
	 */
	public RecsysTVDataSet(JavaRDD<RecsysTVEvent> eventsData, JavaSparkContext sc){
		this.eventsData = eventsData;
		this.sc = sc;
	}
	
	/**
	 * Check if the data is empty
	 * @return true if the data set is empty
	 */
	public boolean isEmpty(){
		return eventsData.isEmpty();
	}
	
	/**
	 * Method that check if a particular event is in the data set. 
	 * The comparison are done by the <method>equals</method> and <method>hashCode</method> methods of the <class>RecsysTVEvent</class>.
	 * @param event The event to be tested if it is in the data set.
	 * @return True if the event is in the data set, false otherwise.
	 */
	public boolean contains(RecsysTVEvent event){
		JavaRDD<RecsysTVEvent> eventRDD = SparkUtilities.<RecsysTVEvent>elementToJavaRDD(event, sc);
		JavaRDD<RecsysTVEvent> intersection = eventsData.intersection(eventRDD);
		return !intersection.isEmpty();
	}
	
	/**
	 * Method that return the list of all distinct user Ids in the data set. 
	 * @return A list of integer representing all the distinct user Ids.
	 */
	public List<Integer> getAllUserIds(){
		return eventsData.map(tvEvent -> tvEvent.getUserID()).distinct().collect();
	}
	
	/**
	 * Method that return the list of all distinct program Ids in the data set. 
	 * @return A list of integer representing all the distinct program Ids.
	 */
	public List<Integer> getAllProgramIds(){
		return eventsData.map(tvEvent -> tvEvent.getProgramID()).distinct().collect();
	}
	
	/**
	 * Method that returns tv events that have at least been viewed minTimeView time.
	 * @param minTimeView The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least minTimeView time.
	 */
	public JavaRDD<RecsysTVEvent> filterByMinTimeView(int minTimeView){
		return eventsData.filter(tvEvent -> tvEvent.getDuration() >= minTimeView);
	}
	
	/**
	 * Method that count all distinct user in the data set.
	 * @return The number of distinct users.
	 */
	public int getNumberOfUsers(){
		return (int)eventsData.map(tvEvent -> tvEvent.getUserID()).distinct().count();
	}
	
	/**
	 * Method that count all distinct items in the data set.
	 * @return The number of distinct items.
	 */
	public int getNumberOfItems(){
		return (int)eventsData.map(tvEvent -> tvEvent.getProgramID()).distinct().count();
	}
	
	/**
	 * Method that converts the data set into the good format for using ml lib methods. 
	 * @return A java RDD of the <class>Rating</class> class.
	 */
	public JavaRDD<Rating> convertToMLlibRatings(){
		return eventsData.map(event -> new Rating(event.getUserID(), event.getProgramID(), 1.0));
	}
	
	/**
	 * Method that converts the data into an RDD of IndexedRow that can be used to do distributed calculus.
	 * @return An IndexedRowMatrix representing the data.
	 */
	public IndexedRowMatrix convertToMLLibUserItemMatrix(){
		UserItemMatrix userItemMatrix = convertToUserItemMatrix();
		JavaRDD<IndexedRow> rows = sc.parallelize(userItemMatrix.getMatrixRowsAsVector());
		return new IndexedRowMatrix(rows.rdd());
	}
	
	public IndexedRowMatrix convertToMLLibItemUserMatrix(){
		UserItemMatrix userItemMatrix = convertToUserItemMatrix();
		JavaRDD<IndexedRow> rows = sc.parallelize(userItemMatrix.getMatrixColumnAsVector());
		return new IndexedRowMatrix(rows.rdd());
	}
	
	/**
	 * Method that returns a Coordinate Matrix containing the cosine similarities between items
	 * @return A CoordinateMatrix containing the similarities.
	 */
	public CoordinateMatrix calculateItemsCosineSimilarities(){
		IndexedRowMatrix rowMatrix = convertToMLLibUserItemMatrix();
		return rowMatrix.columnSimilarities();
	}
	
	/**
	 * Method that returns a Coordinate Matrix containing the cosine similarities between users
	 * @return A CoordinateMatrix containing the similarities.
	 */
	public CoordinateMatrix calculateUsersCosineSimilarities(){
		IndexedRowMatrix columnMatrix = convertToMLLibItemUserMatrix();
		return columnMatrix.columnSimilarities();
	}
	
	public UserItemMatrix convertToUserItemMatrix(){
		UserItemMatrix X = new UserItemMatrix(getNumberOfUsers(), getNumberOfItems());
		eventsData.foreach(tvEvent -> X.setUserSeenItem(RecSysMapReader.mapUserIDtoID(tvEvent.getUserID())
				, RecSysMapReader.mapProgramIDtoID(tvEvent.getProgramID())));
		return X;
	}
	
	/**
	 * Method that releases all resource attached to this dataset.
	 */
	public void close(){
		sc.close();
		eventsData = null;
	}
}
