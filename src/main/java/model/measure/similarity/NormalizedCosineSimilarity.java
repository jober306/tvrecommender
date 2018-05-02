package model.measure.similarity;

import model.measure.NormalizedMeasure;

/**
 * Singleton class used to calculate the normalized cosine similarity between two vectors.
 * @author Jonathan Bergeron
 *
 */
public class NormalizedCosineSimilarity extends NormalizedMeasure{
	
	private static final NormalizedCosineSimilarity instance = new NormalizedCosineSimilarity();
	
	private NormalizedCosineSimilarity() {
		super(CosineSimilarity.instance());
	};
	
	public static NormalizedCosineSimilarity getInstance(){
		return instance;
	}
}
