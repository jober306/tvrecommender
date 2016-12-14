package data.recsys.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.feature.FeatureExtractor;
import data.recsys.model.RecsysTVEvent;
import data.recsys.model.RecsysTVProgram;

public class RecsysFeatureExtractor extends FeatureExtractor<RecsysTVProgram, RecsysTVEvent> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final RecsysFeatureExtractor RECSYS_FEATURE_EXTRACTOR = new RecsysFeatureExtractor();
	
	private RecsysFeatureExtractor(){};
	
	public static RecsysFeatureExtractor getInstance(){
		return RECSYS_FEATURE_EXTRACTOR;
	}
	
	@Override
	public Vector extractFeaturesFromProgram(RecsysTVProgram program) {
		double[] features = new double[3];
		features[0] = program.getChannelId();
		features[1] = program.getGenreId();
		features[2] = program.getSubGenreId();
		return Vectors.dense(features);
	}
	
	@Override
	public Vector extractFeaturesFromEvent(RecsysTVEvent event) {
		double[] features = new double[4];
		features[0] = event.getChannelId();
		features[1] = event.getSlot();
		features[2] = event.getGenreID();
		features[3] = event.getSubgenreID();
		return Vectors.dense(features);
	}
	
}
