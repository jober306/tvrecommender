package model.tensor;

import data.AbstractTVEvent;
import data.TVDataSet;
import data.TVProgram;
import data.feature.FeatureExtractor;

public abstract class UserPreferenceTensorCalculator<T extends TVProgram, U extends AbstractTVEvent<T>> {
	
	abstract public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<T, U> dataSet, FeatureExtractor<T,U> extractor, boolean anyUser, boolean anyProgram, boolean anySlot);
}