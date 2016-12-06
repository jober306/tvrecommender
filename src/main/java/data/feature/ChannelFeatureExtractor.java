package data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.model.TVEvent;

public class ChannelFeatureExtractor<T extends TVEvent> extends FeatureExtractor<T> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public ChannelFeatureExtractor() {};
	
	@Override
	public Vector extractFeatures(TVEvent tvEvent) {
		return Vectors.dense(new double[]{tvEvent.getChannelID()});
	}
	
}
