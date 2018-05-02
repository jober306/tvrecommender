package model.measure.distance;

import model.measure.similarity.CosineSimilarity;

/**
 * Singleton class used to calculate cosine distance.
 * @author Jonathan Bergeron
 *
 */
public class CosineDistance extends DistanceMeasure{
	
	private static final CosineDistance instance = new CosineDistance();
	
	private CosineDistance(){
		super(CosineSimilarity.instance());
	};
	
	public static CosineDistance instance(){
		return instance;
	}
}
