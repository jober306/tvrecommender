package model.measure.distance;

import model.measure.NormalizedMeasure;


public class NormalizedCosineDistance extends NormalizedMeasure{
	
	private static final NormalizedCosineDistance instance = new NormalizedCosineDistance();
	
	private NormalizedCosineDistance() {
		super(CosineDistance.instance());
	};
	
	public static NormalizedCosineDistance getInstance(){
		return instance;
	}
}
