package data.recsys;

import static util.spark.mllib.MllibUtilities.sparseMatrixFormatToCSCMatrixFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.recommendation.Rating;

import data.TVDataSet;
import model.data.User;
import model.data.feature.FeatureExtractor;
import model.matrix.DistributedUserTVProgramMatrix;
import model.matrix.LocalUserTVProgramMatrix;
import model.matrix.UserTVProgramMapping;
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
public class RecsysTVDataSet extends TVDataSet<User, RecsysTVProgram, RecsysTVEvent> implements Serializable{

	/**
	 * The date the data set started recording tv audience behavior (This is not
	 * the real one). Monday, april 1995 at midnigh.
	 */
	public static final LocalDateTime START_TIME = LocalDateTime.of(1995,
			Month.APRIL, 10, 0, 0);

	private static final long serialVersionUID = 1L;

	/**
	 * Main constructor of the class. Use the
	 * <class>RecsysTVDataSetLoader</class> to get the RDD off a csv file.
	 * 
	 * @param eventsData
	 *            The data in spark RDD format.
	 * @param sc
	 *            The java spark context in which the data has been loaded.
	 */
	public RecsysTVDataSet(JavaRDD<RecsysTVEvent> eventsData, JavaSparkContext sc) {
		super(eventsData, sc);
	}

	/**
	 * Method that converts the data set into the good format for using mllib
	 * methods.
	 * 
	 * @return A java RDD of the <class>Rating</class> class.
	 */
	public JavaRDD<Rating> convertToMLlibRatings() {
		JavaRDD<Rating> ratings = eventsData.map(event -> new Rating(event
				.getUserID(), event.getProgram().programId(), 1.0));
		return ratings;
	}

	/**
	 * Method that converts the data set into a distributed user item matrix.
	 * 
	 * @return return the user item matrix in a distributed form corresponding
	 *         to this data set.
	 */
	public DistributedUserTVProgramMatrix<User, RecsysTVProgram> convertToDistUserItemMatrix() {
		final int numberOfTvShows = (int) getNumberOfTvShows();
		Broadcast<UserTVProgramMapping<User, RecsysTVProgram>> broadcastedMapping = sc.broadcast(getUserTVProgramMapping());
		JavaRDD<IndexedRow> ratingMatrix = eventsData
				.mapToPair(
						event -> new Tuple2<Integer, RecsysTVEvent>(
								broadcastedMapping.value().userToIndex(event.getUser()), event))
				.aggregateByKey(
						new HashSet<Tuple2<Integer, Double>>(),
						(list, event) -> {
							list.add(new Tuple2<Integer, Double>(
									broadcastedMapping.value().tvProgramToIndex(event.getProgram()), 1.0d));
							return list;
						}, (list1, list2) -> {
							list1.addAll(list2);
							return list1;
						})
				.map(sparseRowRepresenation -> new IndexedRow(
						sparseRowRepresenation._1(), Vectors.sparse(
								numberOfTvShows, sparseRowRepresenation._2())));
		return new DistributedUserTVProgramMatrix<>(ratingMatrix, getUserTVProgramMapping());
	}
	
	@Override
	public LocalUserTVProgramMatrix<User, RecsysTVProgram> convertToLocalUserItemMatrix() {
		final int numberOfUsers = (int)getNumberOfUsers();
		final int numberOfTvShows = (int)getNumberOfTvShows();
		Broadcast<UserTVProgramMapping<User, RecsysTVProgram>> broadcastedMapping = sc.broadcast(getUserTVProgramMapping());
		List<MatrixEntry> tvShowIdUserIdEvent = eventsData.map(tvEvent -> new MatrixEntry(broadcastedMapping.value().userToIndex(tvEvent.getUser()), broadcastedMapping.value().tvProgramToIndex(tvEvent.getProgram()), 1.0d)).distinct().collect();
		broadcastedMapping.unpersist();
		Tuple3<int[], int[], double[]> matrixData = sparseMatrixFormatToCSCMatrixFormat(numberOfTvShows, tvShowIdUserIdEvent);
		return new LocalUserTVProgramMatrix<>(numberOfUsers, numberOfTvShows, matrixData._1(), matrixData._2(), matrixData._3(), getUserTVProgramMapping());
	}

	/**
	 * Method that returns the content matrix of each tv show.
	 */
	@Override
	public IndexedRowMatrix getContentMatrix(FeatureExtractor<? super RecsysTVProgram, ? super RecsysTVEvent> extractor) {
		Broadcast<UserTVProgramMapping<User, RecsysTVProgram>> broadcastedMapping = sc.broadcast(getUserTVProgramMapping());
		JavaRDD<IndexedRow> contentMatrix = eventsData
				.mapToPair(
						tvEvent -> new Tuple2<RecsysTVProgram, RecsysTVEvent>(tvEvent
								.getProgram(), tvEvent))
				.reduceByKey((tvEvent1, tvEvent2) -> tvEvent1)
				.map(pair -> {
					RecsysTVEvent event = pair._2();
					int programIndex = broadcastedMapping.value().tvProgramToIndex(event.getProgram());
					return new IndexedRow(programIndex, extractor
							.extractFeaturesFromEvent(event));
				});
		broadcastedMapping.unpersist();
		return new IndexedRowMatrix(contentMatrix.rdd());
	}
}
