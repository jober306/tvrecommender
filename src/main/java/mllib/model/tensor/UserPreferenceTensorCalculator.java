package mllib.model.tensor;

import data.model.TVDataSet;
import data.model.TVEvent;

/**
 * Class that calculates the user preference tensor on a given data set. See the
 * <class>UserPreferenceTensor</class> for details on what it is.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCalculator <T extends TVEvent>{
	
	public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<T> dataSet){
		return new UserPreferenceTensorCollection();
	}
}
