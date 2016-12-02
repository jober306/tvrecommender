package mllib.model.tensor;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.model.TVDataSet;
import data.model.TVEvent;

/**
 * Class that calculates the user preference tensor on a given data set. See the
 * <class>UserPreferenceTensor</class> for details on what it is.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCalculator <T extends TVEvent>{
	
	/**
	 * Constructor of the class. It takes a data set and a sprak context to create the collection of user preference tensor associated
	 * with the given data set.
	 * @param dataSet A tv data set of given tv envent type.
	 * @param sc The java spark context used to load the tv data set.
	 * @return The user preference tensor collection.
	 */
	public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<T> dataSet, JavaSparkContext sc){
		UserPreferenceTensorCollectionAccumulator acc = new UserPreferenceTensorCollectionAccumulator();
		JavaSparkContext.toSparkContext(sc).register(acc);
		JavaRDD<UserPreferenceTensor> userPrefTensors = dataSet.getEventsData().map(event -> new UserPreferenceTensor(event.getUserID(), event.getProgramFeatureVector(), event.getSlot()));
		userPrefTensors.foreach(tensor -> acc.add(tensor));
		return acc.value();
	}
}
