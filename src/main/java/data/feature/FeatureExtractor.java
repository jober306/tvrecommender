package data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;

import data.model.TVEvent;
import data.model.TVProgram;

public abstract class FeatureExtractor<T extends TVProgram, U extends TVEvent> implements Serializable{

	private static final long serialVersionUID = 1L;

	abstract public Vector extractFeaturesFromProgram(T tvProgram);
	abstract public Vector extractFeaturesFromEvent(U tvEvent);
}
