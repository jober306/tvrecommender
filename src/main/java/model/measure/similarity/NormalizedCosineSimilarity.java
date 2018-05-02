package model.measure.similarity;

import model.measure.NormalizedMeasure;

public class NormalizedCosineSimilarity extends NormalizedMeasure{
	
	private static final NormalizedCosineSimilarity instance = new NormalizedCosineSimilarity();
	
	private NormalizedCosineSimilarity() {
		super(CosineSimilarity.instance());
	};
	
	public static NormalizedCosineSimilarity getInstance(){
		return instance;
	}
}
