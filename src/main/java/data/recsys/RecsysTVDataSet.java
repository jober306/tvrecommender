package data.recsys;

import static util.MllibUtilities.sparseMatrixFormatToCSCMatrixFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.MapUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.recommendation.Rating;

import data.TVDataSet;
import data.TVProgram;
import data.feature.FeatureExtractor;
import data.recsys.mapper.MapID;
import data.recsys.mapper.RecSysMapCreator;
import data.recsys.mapper.RecSysMapReader;
import model.DistributedUserItemMatrix;
import model.LocalUserItemMatrix;
import scala.Tuple2;
import scala.Tuple3;

/**
 * Class that represents a data set of recsys tv event. The class holds the
 * spark context in which it has been loaded and offer multiple functionalities
 * on the data set.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSet extends TVDataSet<RecsysTVProgram, RecsysTVEvent> implements
		Serializable, MapID {

	/**
	 * The date the data set started recording tv audience behavior (This is not
	 * the real one). Monday, april 1995 at midnigh.
	 */
	public static final LocalDateTime START_TIME = LocalDateTime.of(1995,
			Month.APRIL, 10, 0, 0);

	private static final long serialVersionUID = 1L;

	/**
	 * The map reader that maps userID of the recsysTVDataset to an unique id
	 * between 1 and #of users.
	 */
	transient RecSysMapReader idMap;

	/**
	 * The broadcasted map reader to be used when the map is used with spark
	 * actions.
	 */
	Broadcast<RecSysMapReader> broadcastedIdMap;

	/**
	 * Boolean to check if wether or not the map file have been erased.
	 */
	transient boolean mapClosed;

	/**
	 * Main constructor of the class. Use the
	 * <class>RecsysTVDataSetLoader</class> to get the RDD off a csv file.
	 * 
	 * @param eventsData
	 *            The data in spark RDD format.
	 * @param sc
	 *            The java spark context in which the data has been loaded.
	 */
	public RecsysTVDataSet(JavaRDD<RecsysTVEvent> eventsData,
			JavaSparkContext sc) {
		super(eventsData, sc);
	}

	@Override
	protected void initialize() {
		initializeMapReader();
		broadcastedIdMap = sc.broadcast(idMap);
		mapClosed = false;
	}

	/**
	 * Method that initializes the map reader.
	 */
	public void initializeMapReader() {
		RecSysMapCreator mapCreator = new RecSysMapCreator();
		mapCreator.createUserIDToIDMap(getAllUserIds());
		mapCreator.createProgramIDToIDMap(getAllProgramIds());
		mapCreator.createEventIDToIDMap(getAllEventIds());
		idMap = new RecSysMapReader(mapCreator.getFileNames());
	}

	/**
	 * Method that converts the data set into the good format for using mllib
	 * methods.
	 * 
	 * @return A java RDD of the <class>Rating</class> class.
	 */
	public JavaRDD<Rating> convertToMLlibRatings() {
		JavaRDD<Rating> ratings = eventsData.map(event -> new Rating(event
				.getUserID(), event.getProgramId(), 1.0));
		return ratings;
	}

	/**
	 * Method that converts the data set into a distributed user item matrix.
	 * 
	 * @return return the user item matrix in a distributed form corresponding
	 *         to this data set.
	 */
	public DistributedUserItemMatrix convertToDistUserItemMatrix() {
		final int numberOfTvShows = (int) getNumberOfTvShows();
		JavaRDD<IndexedRow> ratingMatrix = eventsData
				.mapToPair(
						event -> new Tuple2<Integer, RecsysTVEvent>(
								broadcastedIdMap.getValue().getUserIDToIdMap()
										.get(event.getUserID()), event))
				.aggregateByKey(
						new HashSet<Tuple2<Integer, Double>>(),
						(list, event) -> {
							list.add(new Tuple2<Integer, Double>(
									broadcastedIdMap.getValue()
											.getProgramIDtoIDMap()
											.get(event.getProgramId()), 1.0d));
							return list;
						}, (list1, list2) -> {
							list1.addAll(list2);
							return list1;
						})
				.map(sparseRowRepresenation -> new IndexedRow(
						sparseRowRepresenation._1(), Vectors.sparse(
								numberOfTvShows, sparseRowRepresenation._2())));
		return new DistributedUserItemMatrix(ratingMatrix);
	}
	
	@Override
	public LocalUserItemMatrix convertToLocalUserItemMatrix() {
		final int numberOfUsers = (int)getNumberOfUsers();
		final int numberOfTvShows = (int)getNumberOfTvShows();
		List<MatrixEntry> tvShowIdUserIdEvent = eventsData.map(tvEvent -> new MatrixEntry(broadcastedIdMap.getValue().getUserIDToIdMap().get(tvEvent.getUserID()), broadcastedIdMap.getValue().getProgramIDtoIDMap().get(tvEvent.getProgramId()), 1.0d)).distinct().collect();
		Tuple3<int[], int[], double[]> matrixData = sparseMatrixFormatToCSCMatrixFormat(numberOfTvShows, tvShowIdUserIdEvent);
		return new LocalUserItemMatrix(numberOfUsers, numberOfTvShows, matrixData._1(), matrixData._2(), matrixData._3());
	}

	/**
	 * Method that returns the content matrix of each tv show.
	 */
	@Override
	public IndexedRowMatrix getContentMatrix(
			FeatureExtractor<? extends TVProgram, RecsysTVEvent> extractor) {
		JavaRDD<IndexedRow> contentMatrix = eventsData
				.mapToPair(
						tvEvent -> new Tuple2<Integer, RecsysTVEvent>(tvEvent
								.getProgramId(), tvEvent))
				.reduceByKey((tvEvent1, tvEvent2) -> tvEvent1)
				.map(pair -> {
					RecsysTVEvent event = pair._2();
					int programIndex = broadcastedIdMap.value()
							.getProgramIDtoIDMap().get(event.getProgramId());
					return new IndexedRow(programIndex, extractor
							.extractFeaturesFromEvent(event));
				});
		return new IndexedRowMatrix(contentMatrix.rdd());
	}

	public int getOriginalUserID(int mappedID) {
		return (int) MapUtils.invertMap(idMap.getUserIDToIdMap()).get(mappedID);
	}

	public int getOriginalProgramID(int mappedID) {
		return (int) MapUtils.invertMap(idMap.getProgramIDtoIDMap()).get(
				mappedID);
	}

	public int getOriginalEventID(int mappedID) {
		return (int) MapUtils.invertMap(idMap.getEventIDtoIDMap())
				.get(mappedID);
	}

	public int getMappedUserID(int userID) {
		return idMap.getUserIDToIdMap().get(userID);
	}

	public int getMappedProgramID(int programID) {
		return idMap.getProgramIDtoIDMap().get(programID);
	}

	public int getMappedEventID(int eventID) {
		return idMap.getEventIDtoIDMap().get(eventID);
	}

	/**
	 * Method that releases all resource attached to this dataset.
	 */
	public void close() {
		if (!mapClosed) {
			idMap.close();
			mapClosed = true;
			broadcastedIdMap.unpersist();
			broadcastedIdMap.destroy();
		}
		closeSparkContext();
	}

	public void closeMap() {
		if (!mapClosed) {
			idMap.close();
			mapClosed = true;
			broadcastedIdMap.unpersist();
			broadcastedIdMap.destroy();
		}
	}

	public void closeSparkContext() {
		sc.close();
		eventsData = null;
	}

	@Override
	public void finalize() {
		if (!mapClosed) {
			idMap.close();
			broadcastedIdMap.unpersist();
			broadcastedIdMap.destroy();
		}
	}
}
