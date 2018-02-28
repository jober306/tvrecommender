package data.recsys.tensor;

import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensor;
import model.tensor.UserPreferenceTensorCalculator;
import model.tensor.UserPreferenceTensorCollection;
import model.tensor.UserPreferenceTensorCollectionAccumulator;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.TVDataSet;
import data.feature.FeatureExtractor;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

/**
 * Class that calculates the user preference tensor on a given data set. See the
 * <class>UserPreferenceTensor</class> for details on what it is.
 * @author Jonathan Bergeron
 *
 */
public class RecsysUserPreferenceTensorCalculator extends UserPreferenceTensorCalculator<RecsysTVProgram, RecsysTVEvent>{
	
	/**
	 * Constructor of the class. It takes a data set and a sprak context to create the collection of user preference tensor associated
	 * with the given data set.
	 * @param dataSet A tv data set of given tv envent type.
	 * @return The user preference tensor collection.
	 */
	public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<RecsysTVEvent> dataSet, FeatureExtractor<RecsysTVProgram,RecsysTVEvent> extractor, boolean anyUser, boolean anyProgram, boolean anySlot){
		UserPreferenceTensorCollectionAccumulator acc = new UserPreferenceTensorCollectionAccumulator(anyUser, anyProgram, extractor.extractedVectorSize(), anySlot);
		JavaSparkContext.toSparkContext(dataSet.getJavaSparkContext()).register(acc);
		JavaRDD<UserPreferenceTensor> userPrefTensors = dataSet.getEventsData().map(event -> {
			UserPreference userPref = new UserPreference(event.getUserID(), extractor.extractFeaturesFromEvent(event), event.getSlot());
			UserPreferenceTensor tensor = new UserPreferenceTensor(userPref);
			tensor.incrementValue(event.getDuration());
			return tensor;
		});
		userPrefTensors.foreach(tensor -> acc.add(tensor));
		
		return acc.value();
	}
}
