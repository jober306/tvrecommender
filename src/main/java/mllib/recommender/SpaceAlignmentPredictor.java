package mllib.recommender;

import static java.lang.Math.toIntExact;
import mllib.model.DistributedUserItemMatrix;
import mllib.utility.MllibUtilities;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

/**
 * Class that finds the optimal mapping between item content and the item
 * similarities matrix as proposed by the following article:
 * http://www.ijcai.org/Proceedings/15/Papers/475.pdf It can then predicts the
 * similarity between already seen items and new items by using this mapping.
 * 
 * @author Jonathan Bergeron
 *
 */
public class SpaceAlignmentPredictor {

	/**
	 * The rating matrix of size m x n. Where m is number of users and n number
	 * of items.
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

	JavaSparkContext sc;

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
	public SpaceAlignmentPredictor(DistributedUserItemMatrix R, int r,
			IndexedRowMatrix C, JavaSparkContext sc) {
		this.R = R;
		this.r = r;
		this.C = C;
		this.sc = sc;
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

	private void calculateMprime() {
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd = C
				.computeSVD(toIntExact(C.numCols()), true, 0.0d);
		IndexedRowMatrix U = Csvd.U();
		Matrix V = Csvd.V();
		Matrix Vt = V.transpose();
		IndexedRowMatrix S = MllibUtilities
				.getFullySpecifiedSparseIndexRowMatrixFromCoordinateMatrix(
						R.getItemSimilarities(), sc);
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
