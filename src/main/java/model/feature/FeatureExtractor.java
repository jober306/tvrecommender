package model.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;

import model.data.TVEvent;
import model.data.TVProgram;

/**
 * Abstract class that represents a feature extractor, the class extending it
 * must implements method to extract feature from tv program and tv event.
 * 
 * @author Jonathan Bergeron
 *
 * @param <P>
 *            The tv program class from which the features will be extracted.
 * @param <E>
 *            The tv event class from which the features will be extracted.
 */
public abstract class FeatureExtractor<P extends TVProgram, E extends TVEvent<?, ?>>
		implements Serializable {

	private static final long serialVersionUID = 1L;

	abstract public Vector extractFeaturesFromProgram(P tvProgram);

	abstract public Vector extractFeaturesFromEvent(E tvEvent);
	
	abstract public int extractedVectorSize();
}
