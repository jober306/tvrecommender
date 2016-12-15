package mllib.recommender;

import static java.lang.Math.toIntExact;

import static mllib.utility.MllibUtilities.*;

import static list.utility.ListUtilities.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mllib.model.DistributedUserItemMatrix;
import scala.Tuple2;

import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import algorithm.QuickSelect;
import data.feature.FeatureExtractor;
import data.model.EPG;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

/**
 * Class that finds the optimal mapping between item content and the item
 * similarities matrix as proposed by the following article:
 * http://www.ijcai.org/Proceedings/15/Papers/475.pdf It can then predicts the
 * similarity between already seen items and new items by using this mapping.
 * This recommender is used to alleviate cold start problem of tv recommendation.
 * 
 * @author Jonathan Bergeron
 *
 */
public class SpaceAlignmentRecommender<T extends TVProgram, U extends TVEvent> extends TVRecommender<T,U>{
	
	/**
	 * The feature extractor that will be used to extract features when training the model and whne predicting.
	 */
	FeatureExtractor<T,U> extractor;
	
	/**
	 * The user item (or rating) matrix that represents the tv data set.
	 */
	public DistributedUserItemMatrix R;

	/**
	 * The already seen item contents matrix. Suppose each item is represented
	 * by d features, then the matrix C is of size n x d.
	 */
	IndexedRowMatrix C;
	
	/**
	 * Local version of this indexed row matrix.
	 */
	List<Vector> localC;

	/**
	 * This parameter indicates the maximum rank the matrix Mprime can
	 * have. Note that Mprime has maximum rank d, So r should be
	 * between 1 and d. The higher r is set the better it will fit the item
	 * similarity matrix but higher the chance it will overfit it. On the other
	 * hand if it is too low it could be impossible to represent the item
	 * similarity distribution properly.
	 */
	int r;
	
	/**
	 * The neighbourhood size used when calculating similarity between items.
	 */
	int neighbourhoodSize;

	/**
	 * This matrix of size d x d represents the model to map a new item content
	 * and an ancient item content to their similarity in the collaborative
	 * filtering sense. It is calculated once the class is created.
	 */
	Matrix Mprime;

	/**
	 * Constructor of the <class>SpaceAlignmentPredictor</class>, it calculates
	 * the matrix Mprime with the ratings matrix and the corresponding content
	 * matrix and also takes into account the parameter r.
	 * 
	 * @param R
	 *            The rating matrix.
	 * @param r
	 *            The maximum rank the matrix Mprime can have.
	 * @param C
	 *            The content matrix of all the items.
	 */
	public SpaceAlignmentRecommender(EPG<T> epg, TVDataSet<U> tvDataSet, FeatureExtractor<T,U> extractor, int r, int neighbourhoddSize) {
		super(epg, tvDataSet);
		this.extractor = extractor;
		this.r = r;
		this.neighbourhoodSize = neighbourhoddSize;
		this.R = tvDataSet.convertToDistUserItemMatrix();
		this.C = tvDataSet.getContentMatrix(extractor);
		this.localC = getSecondArgument(C.rows().toJavaRDD().mapToPair(row -> new Tuple2<Integer, Vector>(toIntExact(row.index()), indexedRowToDenseVector(row))).
				sortByKey().collect());
		calculateMprime();
	}

	/**
	 * Method that predicts the similarity between two items with the Mprime
	 * that was calculated.
	 * 
	 * @param coldStartItemContent
	 *            The content vector of the cold start item.
	 * @param oldItemIndex
	 *            The old item index in the content matrix C.
	 * @return The approximated similarity in a collaborative filtering sense
	 *         between the new item and the old item.
	 */
	public double predictItemsSimilarity(Vector coldStartItemContent,
			int oldItemIndex) {
		Vector targetItem = localC.get(oldItemIndex);
		return scalarProduct(Mprime.multiply(coldStartItemContent),targetItem);
	}

	/**
	 * Method that returns the neighborhood of a new item for a specific user.
	 * It returns the top n item indices and values in decreasing order.
	 * 
	 * @param coldStartItemContent
	 *            The content of the new item.
	 * @param userIndex
	 *            The index of the user (the neighborhood returned will only
	 *            contains items seen by this user).
	 * @param n
	 *            The size of the neighborhoods set.
	 * @return A list of pair containing respectively the item index in the user
	 *         item matrix and the similarity value.
	 */
	public List<Tuple2<Integer, Double>> predictNewItemNeighborhoodForUser(
			Vector coldStartItemContent, int userIndex, int n) {
		int[] itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		double[] itemSeenByUserSimilarities = predictAllItemSimilarities(
				coldStartItemContent, itemIndexesSeenByUser);
		return QuickSelect.selectTopN(itemIndexesSeenByUser,
				itemSeenByUserSimilarities,
				Math.min(n, itemSeenByUserSimilarities.length));
	}
	
	/**
	 * Method that returns the neighborhood of a new item.
	 * It returns the top n item indices and values in decreasing order.
	 * 
	 * @param coldStartItemContent
	 *            The content of the new item.
	 * @param n
	 *            The size of the neighborhoods set.
	 * @return A list of pair containing respectively the item index and the item similarity value.
	 */
	public List<Tuple2<Integer, Double>> predictNewItemNeighbourhood(Vector coldStartItemContent, int n){
		Double[] similarities = predictAllItemSimilarities(coldStartItemContent);
		return QuickSelect.selectTopN(similarities, n);
	}
	
	private double calculateNeighboursScore(List<Tuple2<Integer, Double>> neighbours){
		double score = 0.0d;
		for(Tuple2<Integer, Double> neighbour : neighbours){
			score += neighbour._2();
		}
		if(neighbours.size() == 0)
			return score;
		return score / (double)neighbours.size();
	}

	/**
	 * Method that returns the indexes in decreasing order of the show recommended. The indexes
	 * correspond to the newTVShowsContent indexes.
	 * @param userId The user id to which the recommendation will be done.
	 * @param numberOfResults The number of results to return.
	 * @param newTvShowsContent An array of vector containing the content of new tv shows.
	 * @param n The number of items neighbors that will be used to calculate similarity with new items.
	 * @return The indexes in decreasing order from best of the best tv show.
	 */
	public List<Integer> recommend(int userId, LocalDateTime targetWatchTime, int numberOfResults) {
		List<Tuple2<Integer, Vector>> newTvShows = epg.getListProgramsAtWatchTime(targetWatchTime).stream().map(program -> new Tuple2<Integer,Vector>(program.getProgramId(), extractor.extractFeaturesFromProgram(program))).collect(Collectors.toList());
		Double[] neighboursScores = new Double[newTvShows.size()];
		for(int i = 0; i < newTvShows.size(); i++){
			List<Tuple2<Integer, Double>> neighbours = predictNewItemNeighborhoodForUser(newTvShows.get(i)._2(), userId, neighbourhoodSize);
			neighboursScores[i] = calculateNeighboursScore(neighbours);
		}
		List<Tuple2<Integer, Double>> sortedScore = QuickSelect.selectTopN(neighboursScores, numberOfResults);
		return getFirstArgument(sortedScore);
	}

	private double[] predictAllItemSimilarities(Vector coldStartItemContent,
			int[] itemIndexes) {
		double[] similarities = new double[itemIndexes.length];
		for (int i = 0; i < itemIndexes.length; i++) {
			similarities[i] = predictItemsSimilarity(coldStartItemContent,
					itemIndexes[i]);
		}
		return similarities;
	}
	
	private Double[] predictAllItemSimilarities(Vector coldStartItemContent) {
		int numberOfItems = dataSet.getNumberOfItems();
		Double[] similarities = new Double[numberOfItems];
		for (int i = 0; i < numberOfItems; i++) {
			similarities[i] = predictItemsSimilarity(coldStartItemContent,i);
		}
		return similarities;
	}

	private void calculateMprime() {
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd = C
				.computeSVD(toIntExact(C.numCols()), true, 0.0d);
		IndexedRowMatrix U = Csvd.U();
		Matrix V = Csvd.V();
		IndexedRowMatrix S = getFullySpecifiedSparseIndexRowMatrixFromCoordinateMatrix(R.getItemSimilarities(), dataSet.getJavaSparkContext());
		Vector sigma = Csvd.s();
		Vector invertedSigma = invertVector(sigma);
		IndexedRowMatrix Ut = transpose(U);
		// *********************************Intermediate// operations*********************************************
		IndexedRowMatrix leftMat = multiplicateByLeftDiagonalMatrix(invertedSigma, Ut);
		IndexedRowMatrix rightMat = multiplicateByRightDiagonalMatrix(U, Csvd.s());
		Matrix localRightMat = toDenseLocalMatrix(S.multiply(toDenseLocalMatrix(rightMat)));
		IndexedRowMatrix intermediateMat = leftMat.multiply(localRightMat);
		// ***************************************************************************************************
		SingularValueDecomposition<IndexedRowMatrix, Matrix> intMatsvd = intermediateMat
				.computeSVD(r, true, 0.0d);
		Matrix Q = intMatsvd.V();
		DenseMatrix VQ = V.multiply((DenseMatrix)Matrices.dense(Q.numRows(), Q.numCols(), Q.toArray()));
		DenseMatrix QtVt = VQ.transpose();
		Vector hardTrhesholdedLambda = intMatsvd.s();
		Mprime = multiplyMatrixByRightDiagonalMatrix(VQ, hardTrhesholdedLambda).multiply(QtVt);
	}

	private Vector invertVector(Vector v) {
		double[] values = v.copy().toArray();
		for (int i = 0; i < values.length; i++) {
			values[i] = 1.0d / values[i];
		}
		return Vectors.dense(values);
	}
}
