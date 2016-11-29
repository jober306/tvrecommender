package mllib.model.tensor;

import data.model.TVDataSet;
import data.model.TVEvent;

/**
 * Class that calculates the user preference tensor on a given data set.
 * The user preference tensor p_ufs is the total number of minutes user u spent
 * watching a program with features f in time slot s.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCalculator <T extends TVEvent>{
	
	public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<T> dataSet){
		return new UserPreferenceTensorCollection();
	}
}
