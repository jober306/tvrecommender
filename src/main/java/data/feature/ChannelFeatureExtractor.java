package data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.model.TVEvent;
import data.model.TVProgram;

public class ChannelFeatureExtractor<T extends TVProgram, U extends TVEvent> extends FeatureExtractor<T,U> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public ChannelFeatureExtractor() {};
	
	@Override
	public Vector extractFeaturesFromProgram(TVProgram program) {
		return Vectors.dense(new double[]{program.getChannelId()});
	}
	
	@Override
	public Vector extractFeaturesFromEvent(TVEvent event) {
		return Vectors.dense(new double[]{event.getChannelId()});
	}
	
}
