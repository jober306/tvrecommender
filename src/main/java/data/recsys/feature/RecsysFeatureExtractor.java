package data.recsys.feature;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import model.data.feature.FeatureExtractor;

/**
 * A feature extractor singleton class for the recsys data set.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysFeatureExtractor extends
		FeatureExtractor<RecsysTVProgram, RecsysTVEvent> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private static final RecsysFeatureExtractor RECSYS_FEATURE_EXTRACTOR = new RecsysFeatureExtractor();

	private RecsysFeatureExtractor() {
	};

	/**
	 * Method to access the singleton feature extractor
	 * 
	 * @return The singleton feature extractor.
	 */
	public static RecsysFeatureExtractor getInstance() {
		return RECSYS_FEATURE_EXTRACTOR;
	}

	/**
	 * Method that extracts the principal features of a recsys tv program.
	 * 
	 * @param program
	 *            The recsys tv program.
	 * @return Return a vector containing the channel id, the genre id and the
	 *         sub genre id.
	 */
	@Override
	public Vector extractFeaturesFromProgram(RecsysTVProgram program) {
		double[] features = new double[4];
		features[0] = program.channelId();
		features[1] = program.slot();
		features[2] = program.genreId();
		features[3] = program.subGenreId();
		return Vectors.dense(features);
	}

	/**
	 * Method that extracts the principal features of a recsys tv event.
	 * 
	 * @param program
	 *            The recsys tv event.
	 * @return Return a vector containing the channel id, the slot, the genre id
	 *         and the sub genre id.
	 */
	@Override
	public Vector extractFeaturesFromEvent(RecsysTVEvent event) {
		double[] features = new double[4];
		features[0] = event.channelId();
		features[1] = event.getSlot();
		features[2] = event.getGenreID();
		features[3] = event.getSubgenreID();
		return Vectors.dense(features);
	}
	
	@Override
	public int extractedVectorSize(){
		return 4;
	}
}
