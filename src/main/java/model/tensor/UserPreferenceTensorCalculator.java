package model.tensor;

import data.TVDataSet;
import model.data.TVEvent;
import model.data.TVProgram;
import model.data.feature.FeatureExtractor;

public abstract class UserPreferenceTensorCalculator<P extends TVProgram, E extends TVEvent<?, P>> {
	
	abstract public UserPreferenceTensorCollection calculateUserPreferenceTensorForDataSet(TVDataSet<?, ? extends P, ? extends E> dataSet, FeatureExtractor<? super P, ? super E> extractor, boolean anyUser, boolean anyProgram, boolean anySlot);
}