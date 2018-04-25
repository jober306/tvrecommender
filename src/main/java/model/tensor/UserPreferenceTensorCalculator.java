package model.tensor;

import data.TVDataSet;
import model.data.TVEvent;
import model.data.TVProgram;
import model.feature.FeatureExtractor;

public abstract class UserPreferenceTensorCalculator<T extends TVProgram, U extends TVEvent<T>> {
	
	abstract public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<T, U> dataSet, FeatureExtractor<? super T, ? super U> extractor, boolean anyUser, boolean anyProgram, boolean anySlot);
}