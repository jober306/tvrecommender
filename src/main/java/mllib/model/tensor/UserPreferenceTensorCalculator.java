package mllib.model.tensor;

import data.feature.FeatureExtractor;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

public abstract class UserPreferenceTensorCalculator<T extends TVProgram, U extends TVEvent> {
	
	abstract public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<U> dataSet, FeatureExtractor<T,U> extractor);
}
