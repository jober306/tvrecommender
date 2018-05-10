package model.data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import model.data.TVEvent;
import model.data.TVProgram;

public class ChannelTVProgramIdsFeatureExtractor extends FeatureExtractor<TVProgram, TVEvent<?, ?>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static ChannelTVProgramIdsFeatureExtractor INSTANCE = new ChannelTVProgramIdsFeatureExtractor();
	
	private ChannelTVProgramIdsFeatureExtractor() {};
	
	public static ChannelTVProgramIdsFeatureExtractor instance() {
		return INSTANCE;
	}

	@Override
	public Vector extractFeaturesFromProgram(TVProgram tvProgram) {
		return Vectors.dense(new double[]{tvProgram.channelId(), tvProgram.id()});

	}

	@Override
	public Vector extractFeaturesFromEvent(TVEvent<?, ?> tvEvent) {
		return Vectors.dense(new double[]{tvEvent.channelId(), tvEvent.programID()});

	}

	@Override
	public int extractedVectorSize() {
		return 2;
	}

}
