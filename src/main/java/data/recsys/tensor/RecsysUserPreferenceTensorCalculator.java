package data.recsys.tensor;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.TVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.data.feature.FeatureExtractor;
import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensor;
import model.tensor.UserPreferenceTensorCalculator;
import model.tensor.UserPreferenceTensorCollection;
import model.tensor.UserPreferenceTensorCollectionAccumulator;

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
	@Override
	public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<?, ? extends RecsysTVProgram, ? extends RecsysTVEvent> dataSet, FeatureExtractor<? super RecsysTVProgram, ? super RecsysTVEvent> extractor, boolean anyUser, boolean anyProgram, boolean anySlot){
		UserPreferenceTensorCollectionAccumulator acc = new UserPreferenceTensorCollectionAccumulator(anyUser, anyProgram, extractor.extractedVectorSize(), anySlot);
		JavaSparkContext.toSparkContext(dataSet.javaSparkContext()).register(acc);
		JavaRDD<UserPreferenceTensor> userPrefTensors = dataSet.events().map(event -> {
			UserPreference userPref = new UserPreference(event.userID(), extractor.extractFeaturesFromEvent(event), event.getSlot());
			UserPreferenceTensor tensor = new UserPreferenceTensor(userPref);
			tensor.incrementValue(event.watchDuration());
			return tensor;
		});
		userPrefTensors.foreach(tensor -> acc.add(tensor));
		return acc.value();
	}
}
