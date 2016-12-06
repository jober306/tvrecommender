package data.recsys.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.feature.FeatureExtractor;
import data.recsys.model.RecsysTVEvent;

public class RecsysFeatureExtractor extends FeatureExtractor<RecsysTVEvent> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final RecsysFeatureExtractor RECSYS_FEATURE_EXTRACTOR = new RecsysFeatureExtractor();
	
	private RecsysFeatureExtractor(){};
	
	public static RecsysFeatureExtractor getInstance(){
		return RECSYS_FEATURE_EXTRACTOR;
	}
	
	@Override
	public Vector extractFeatures(RecsysTVEvent tvEvent) {
		double[] features = new double[4];
		features[0] = tvEvent.getChannelID();
		features[1] = tvEvent.getSlot();
		features[2] = tvEvent.getGenreID();
		features[3] = tvEvent.getSubgenreID();
		return Vectors.dense(features);
	}
	
}
