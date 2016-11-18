package mllib.recommender;

import static java.lang.Math.toIntExact;

import java.util.List;

import mllib.model.DistributedUserItemMatrix;
import mllib.utility.MllibUtilities;

import org.apache.commons.math3.util.Pair;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import algorithm.QuickSelect;
import data.model.TVDataSet;
import data.model.TVEvent;

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
public class SpaceAlignmentRecommender <T extends TVEvent>{
	
	/**
	 * The tv data set on which the matrix M prime will be build.
	 */
	TVDataSet<T> tvDataset;
	
	/**
	 * The user item (or rating) matrix that represents the tv data set.
	 */
	DistributedUserItemMatrix R;

	/**
	 * The already seen item contents matrix. Suppose each item is represented
	 * by d features, then the matrix C is of size n x d.
	 */
	IndexedRowMatrix C;

	/**
	 * This parameter indicates the maximum rank that the matrix Mprime can
	 * have. Note that by its size Mprime has maximum rank d, So r should be
	 * between 1 and d. The higher it is the best it will fit the item
	 * similarity matrix but the higher the chance it will overfit. On the other
	 * hand if it is too high it could be impossible to represent the item
	 * similarity distribution properly.
	 */
	int r;

	/**
	 * This matrix of size d x d represents the model to map a new item content
	 * and an ancient item content to their similarity in the collaborative
	 * filtering sense. It is calculated once the class is created.
	 */
	IndexedRowMatrix Mprime;

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
	public SpaceAlignmentRecommender(TVDataSet<T> tvDataSet, int r) {
		this.tvDataset = tvDataSet;
		this.r = r;
		this.R = tvDataSet.convertToDistUserItemMatrix();
		this.C = tvDataSet.getContentMatrix();
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
		Vector targetItem = MllibUtilities.indexedRowToDenseVector(C.rows()
				.toJavaRDD().filter(row -> row.index() == oldItemIndex)
				.collect().get(0));
		return MllibUtilities.scalarProduct(MllibUtilities
				.multiplyColumnVectorByMatrix(Mprime, coldStartItemContent),
				targetItem);
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
	public List<Pair<Integer, Double>> predictNewItemNeighborhoodForUser(
			Vector coldStartItemContent, int userIndex, int n) {
		int[] itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		double[] itemSeenByUserSimilarities = predictAllItemSimilarities(
				coldStartItemContent, itemIndexesSeenByUser);
		return QuickSelect.selectTopN(itemIndexesSeenByUser,
				itemSeenByUserSimilarities,
				Math.min(n, itemSeenByUserSimilarities.length));
	}
	
	private double calculateNeighboursScore(List<Pair<Integer, Double>> neighbours){
		double score = 0.0d;
		for(Pair<Integer, Double> neighbour : neighbours){
			score += neighbour.getSecond();
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
	 * @return The indexes in decreasing order from best of the best tv show.
	 */
	public int[] recommend(int userId, int numberOfResults, Vector[] newTvShowsContent, int n) {
		Double[] neighboursScores = new Double[newTvShowsContent.length];
		for(int i = 0; i < newTvShowsContent.length; i++){
			List<Pair<Integer, Double>> neighbours = predictNewItemNeighborhoodForUser(newTvShowsContent[i], userId, n);
			neighboursScores[i] = calculateNeighboursScore(neighbours);
		}
		List<Pair<Integer, Double>> sortedScore = QuickSelect.selectTopN(neighboursScores, numberOfResults);
		int[] recommendationIndexes = new int[numberOfResults];
		for(int i = 0; i < numberOfResults; i++){
			recommendationIndexes[i] = sortedScore.get(i).getFirst();
		}
		return recommendationIndexes;
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

	private void calculateMprime() {
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd = C
				.computeSVD(toIntExact(C.numCols()), true, 0.0d);
		IndexedRowMatrix U = Csvd.U();
		Matrix V = Csvd.V();
		Matrix Vt = V.transpose();
		IndexedRowMatrix S = MllibUtilities
				.getFullySpecifiedSparseIndexRowMatrixFromCoordinateMatrix(
						R.getItemSimilarities(), tvDataset.getJavaSparkContext());
		Vector sigma = Csvd.s();
		Vector invertedSigma = invertVector(sigma);
		IndexedRowMatrix Ut = MllibUtilities.transpose(U);
		// *********************************Intermediate
		// operations*********************************************
		IndexedRowMatrix leftMat = MllibUtilities
				.multiplicateByLeftDiagonalMatrix(invertedSigma, Ut);
		IndexedRowMatrix rightMat = MllibUtilities
				.multiplicateByRightDiagonalMatrix(U, Csvd.s());
		Matrix localS = MllibUtilities.toDenseLocalMatrix(S);
		Matrix localRightMat = MllibUtilities.toDenseLocalMatrix(rightMat);
		IndexedRowMatrix intermediateMat = leftMat.multiply(localS).multiply(
				localRightMat);
		// ***************************************************************************************************
		SingularValueDecomposition<IndexedRowMatrix, Matrix> intMatsvd = intermediateMat
				.computeSVD(toIntExact(intermediateMat.numRows()), true,
						1.0E-9d);
		IndexedRowMatrix Q = intMatsvd.U();
		Vector hardTrhesholdedLambda = MllibUtilities.hardThreshold(
				intMatsvd.s(), r);
		IndexedRowMatrix QtVt = MllibUtilities.transpose(Q).multiply(Vt);
		IndexedRowMatrix VQ = MllibUtilities.transpose(QtVt);
		Mprime = MllibUtilities.multiplicateByRightDiagonalMatrix(VQ,
				hardTrhesholdedLambda).multiply(
				MllibUtilities.toDenseLocalMatrix(QtVt));
	}

	private Vector invertVector(Vector v) {
		double[] values = v.toArray();
		for (int i = 0; i < values.length; i++) {
			values[i] = 1.0d / values[i];
		}
		return Vectors.dense(values);
	}
}
