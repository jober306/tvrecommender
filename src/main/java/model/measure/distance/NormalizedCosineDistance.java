package model.measure.distance;

import model.measure.NormalizedMeasure;

/**
 * Singleton class used to calculate the normalized cosine distance between two vectors.
 * @author Jonathan Bergeron
 *
 */
public class NormalizedCosineDistance extends NormalizedMeasure{
	
	private static final NormalizedCosineDistance instance = new NormalizedCosineDistance();
	
	private NormalizedCosineDistance() {
		super(CosineDistance.instance());
	};
	
	public static NormalizedCosineDistance getInstance(){
		return instance;
	}
}
