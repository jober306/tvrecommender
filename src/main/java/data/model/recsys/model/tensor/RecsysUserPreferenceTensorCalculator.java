package data.model.recsys.model.tensor;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.feature.FeatureExtractor;
import data.model.TVDataSet;
import data.recsys.model.RecsysTVEvent;
import data.recsys.model.RecsysTVProgram;
import mllib.model.tensor.UserPreferenceTensor;
import mllib.model.tensor.UserPreferenceTensorCalculator;
import mllib.model.tensor.UserPreferenceTensorCollection;
import mllib.model.tensor.UserPreferenceTensorCollectionAccumulator;

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
	public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<RecsysTVEvent> dataSet, FeatureExtractor<RecsysTVProgram,RecsysTVEvent> extractor){
		UserPreferenceTensorCollectionAccumulator acc = new UserPreferenceTensorCollectionAccumulator();
		JavaSparkContext.toSparkContext(dataSet.getJavaSparkContext()).register(acc);
		JavaRDD<UserPreferenceTensor> userPrefTensors = dataSet.getEventsData().map(event -> {
			UserPreferenceTensor tensor = new UserPreferenceTensor(event.getUserID(), extractor.extractFeaturesFromEvent(event), event.getSlot());
			tensor.incrementValue(event.getDuration());
			return tensor;
		});
		userPrefTensors.foreach(tensor -> acc.add(tensor));
		return acc.value();
	}
}
