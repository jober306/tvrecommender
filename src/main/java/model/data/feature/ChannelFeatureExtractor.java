package model.data.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import model.data.TVEvent;
import model.data.TVProgram;

/**
 * A feature extractor class that returns the channel as feature vector.
 * @author Jonathan Bergeron
 *
 * @param <T> A child class of the tv program class.
 * @param <U> A child class of the tv event class.
 */
public class ChannelFeatureExtractor extends FeatureExtractor<TVProgram, TVEvent<?, ?>> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static ChannelFeatureExtractor INSTANCE = new ChannelFeatureExtractor();
	
	private ChannelFeatureExtractor() {};
	
	public static ChannelFeatureExtractor instance() {
		return INSTANCE;
	}
	
	/**
	 * Method that extracts the channel from the tv program.
	 * @param program The program from which the feature will be extracted
	 * @return A vector containing the channel id. 
	 */
	@Override
	public Vector extractFeaturesFromProgram(TVProgram program) {
		return Vectors.dense(new double[]{program.channelId()});
	}
	
	/**
	 * Method that extracts the channel from the tv event.
	 * @param event The tv event from which the feature will be extracted.
	 * @return A vector containing the channel id. 
	 */
	@Override
	public Vector extractFeaturesFromEvent(TVEvent<?, ?> event) {
		return Vectors.dense(new double[]{event.channelId()});
	}
	
	@Override
	public int extractedVectorSize(){
		return 1;
	}
	
}
