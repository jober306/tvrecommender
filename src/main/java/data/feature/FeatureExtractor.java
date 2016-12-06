package data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;

import data.model.TVEvent;

public abstract class FeatureExtractor<T extends TVEvent> implements Serializable{

	private static final long serialVersionUID = 1L;

	abstract public Vector extractFeatures(T tvEvent);
}
