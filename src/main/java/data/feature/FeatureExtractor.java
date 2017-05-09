package data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;

import data.TVEvent;
import data.TVProgram;

/**
 * Abstract class that represents a feature extractor, the class extending it
 * must implements method to extract feature from tv program and tv event.
 * 
 * @author Jonathan Bergeron
 *
 * @param <T>
 *            The tv program class from which the features will be extracted.
 * @param <U>
 *            The tv event class from which the features will be extracted.
 */
public abstract class FeatureExtractor<T extends TVProgram, U extends TVEvent>
		implements Serializable {

	private static final long serialVersionUID = 1L;

	abstract public Vector extractFeaturesFromProgram(T tvProgram);

	abstract public Vector extractFeaturesFromEvent(U tvEvent);

	public void print(Vector vector) {

	}
}
