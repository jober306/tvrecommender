package data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.model.TVEvent;

public class ChannelFeatureExtractor extends FeatureExtractor<TVEvent> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static ChannelFeatureExtractor CHANNEL_FEATURE_EXTRACTOR = new ChannelFeatureExtractor();
	
	private ChannelFeatureExtractor() {};
	
	public static ChannelFeatureExtractor getInstance() {
		return CHANNEL_FEATURE_EXTRACTOR;
	}
	
	@Override
	public Vector extractFeatures(TVEvent tvEvent) {
		return Vectors.dense(new double[]{tvEvent.getChannelID()});
	}
	
}
