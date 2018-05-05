package data.recsys.feature;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import scala.Tuple2;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.utility.RecsysUtilities;
import model.data.feature.FeatureExtractor;

/**
 * A feature extractor for the recsys data set. It transforms a tv program or tv
 * event into a boolean vector. Each entry of the feature vector represents
 * either a channel, a genre, a subgenre or a slot. The map in RecsysUtilities class indicates
 * wich index correspond to which channel, genre, subgenre or slot.
 * 
 * The extracted vector will have exactly four 1s and the rest will be zero.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysBooleanFeatureExtractor extends FeatureExtractor<RecsysTVProgram, RecsysTVEvent> {
	
	private static final long serialVersionUID = 1L;
	
	final static RecsysBooleanFeatureExtractor INSTANCE = new RecsysBooleanFeatureExtractor();
	
	private RecsysBooleanFeatureExtractor() {}
	
	public static RecsysBooleanFeatureExtractor instance() {
		return INSTANCE;
	}
	
	/**
	 * Method that returns the feature vector of a given tv program. The feature
	 * vector will contain 3 ones representing the channel, genre and subgenre
	 * of the tv program.
	 * 
	 * @param tvProgram
	 *            The tv program from which a feature vector needs to be
	 *            extracted.
	 * @return The feature vector representing this tv program.
	 */
	@Override
	public Vector extractFeaturesFromProgram(RecsysTVProgram tvProgram) {
		short channelID = tvProgram.channelId();
		short slot = tvProgram.slot();
		byte genreID = tvProgram.genreId();
		byte subgenreID = tvProgram.subGenreId();
		return createBooleanFeatureVector(channelID, slot, genreID, subgenreID);
	}

	/**
	 * Method that returns the feature vector of a given tv event. The feature
	 * vector will contain 3 ones representing the channel, genre and subgenre
	 * of the tv event.
	 * 
	 * @param tvEvent
	 *            The tv event from which a feature vector needs to be
	 *            extracted.
	 * @return The feature vector representing this tv event.
	 */
	@Override
	public Vector extractFeaturesFromEvent(RecsysTVEvent tvEvent) {
		short channelID = tvEvent.channelId();
		short slot = tvEvent.getSlot();
		byte genreID = tvEvent.getGenreID();
		byte subgenreID = tvEvent.getSubgenreID();
		return createBooleanFeatureVector(channelID, slot, genreID, subgenreID);
	}
	
	@Override
	public int extractedVectorSize(){
		return RecsysUtilities.getChannelIDMap().size() + RecsysUtilities.getSlotIDMap().size() + RecsysUtilities.getGenreIDMap().size() + RecsysUtilities.getSubgenreIDMap().size();
	}
	
	private Vector createBooleanFeatureVector(short channelID, short slot, byte genreID, byte subgenreID) {
		List<Tuple2<Integer, Double>> features = new ArrayList<Tuple2<Integer, Double>>();
		features.add(new Tuple2<Integer, Double>(RecsysUtilities.getChannelIDMap().get(channelID), 1.0d));
		features.add(new Tuple2<Integer, Double>(RecsysUtilities.getSlotIDMap().get(slot), 1.0d));
		features.add(new Tuple2<Integer, Double>(RecsysUtilities.getGenreIDMap().get(genreID), 1.0d));
		features.add(new Tuple2<Integer, Double>(RecsysUtilities.getSubgenreIDMap().get(subgenreID), 1.0d));
		return Vectors.sparse(extractedVectorSize(), features);
	}
}
