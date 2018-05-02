package model.measure.distance;

import model.measure.similarity.CosineSimilarity;

public class CosineDistance extends DistanceMeasure{
	
	private static final CosineDistance instance = new CosineDistance();
	
	private CosineDistance(){
		super(CosineSimilarity.instance());
	};
	
	public static CosineDistance instance(){
		return instance;
	}
}
