package data.recsys.feature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import scala.Tuple2;
import data.feature.FeatureExtractor;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

/**
 * A feature extractor for the recsys data set. It transforms a tv program or tv
 * event into a boolean vector. Each entry of the feature vector represents
 * either a channel, a genre or a subgenre. Therefore, the feature vector will
 * contain exactly 3 ones and the rest will be zeros.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysBooleanFeatureExtractor extends
		FeatureExtractor<RecsysTVProgram, RecsysTVEvent> implements
		Serializable {

	private static final long serialVersionUID = 5644373733120859321L;

	Map<Integer, Integer> channelIDMap;
	Map<Byte, Integer> genreIDMap;
	Map<Byte, Integer> subgenreIDMap;

	private int mappedID;

	/**
	 * Constructor of the recsys boolean feature extractor, it constructs a
	 * mapping from the channel, genre and subgenre ids to their index in the
	 * feature vector.
	 * 
	 * @param epg
	 *            The electronic programming guide from which the channel id
	 *            mapping will be created.
	 */
	public RecsysBooleanFeatureExtractor(RecsysEPG epg) {
		this.mappedID = 0;
		initializeChannelIDMap(epg);
		initializeGenreIDMap();
		initializeSubgenreIDMap();
	}

	private void initializeChannelIDMap(RecsysEPG epg) {
		Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
		List<Integer> channelIDs = epg.getEPG()
				.map(program -> program.getChannelId()).distinct().collect();
		for (int channelID : channelIDs) {
			tempMap.put(channelID, mappedID);
			mappedID++;
		}
		this.channelIDMap = Collections.unmodifiableMap(tempMap);
	}

	private void initializeGenreIDMap() {
		Map<Byte, Integer> tempMap = new HashMap<Byte, Integer>();
		for (byte genreID = 1; genreID <= 8; genreID++) {
			tempMap.put(genreID, mappedID);
			mappedID++;
		}
		this.genreIDMap = Collections.unmodifiableMap(tempMap);
	}

	private void initializeSubgenreIDMap() {
		Map<Byte, Integer> tempMap = new HashMap<Byte, Integer>();
		for (byte subgenreID = 1; subgenreID <= 114; subgenreID++) {
			tempMap.put(subgenreID, mappedID);
			mappedID++;
		}
		this.subgenreIDMap = Collections.unmodifiableMap(tempMap);
	}

	/**
	 * Getter method that returns the channelIDMap. The map is read-only.
	 * 
	 * @return The read-only channelIDMap
	 */
	public Map<Integer, Integer> getChannelIDMap() {
		return channelIDMap;
	}

	/**
	 * Getter method that returns the genreIDMap. The map is read-only.
	 * 
	 * @return The read-only genreIDMap
	 */
	public Map<Byte, Integer> getGenreIDMap() {
		return genreIDMap;
	}

	/**
	 * Getter method that returns the subgenreIDMap. The map is read-only.
	 * 
	 * @return The read-only subgenreIDMap
	 */
	public Map<Byte, Integer> getSubgenreIDMap() {
		return subgenreIDMap;
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
		int channelID = tvProgram.getChannelId();
		byte genreID = tvProgram.getGenreId();
		byte subgenreID = tvProgram.getSubGenreId();
		return createBooleanFeatureVector(channelID, genreID, subgenreID);
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
		int channelID = tvEvent.getChannelId();
		byte genreID = tvEvent.getGenreID();
		byte subgenreID = tvEvent.getSubgenreID();
		return createBooleanFeatureVector(channelID, genreID, subgenreID);
	}

	private Vector createBooleanFeatureVector(int channelID, byte genreID,
			byte subgenreID) {
		List<Tuple2<Integer, Double>> features = new ArrayList<Tuple2<Integer, Double>>();
		features.add(new Tuple2<Integer, Double>(channelIDMap.get(channelID),
				1.0d));
		features.add(new Tuple2<Integer, Double>(genreIDMap.get(genreID), 1.0d));
		features.add(new Tuple2<Integer, Double>(subgenreIDMap.get(subgenreID),
				1.0d));
		return Vectors.sparse(mappedID, features);
	}
}
