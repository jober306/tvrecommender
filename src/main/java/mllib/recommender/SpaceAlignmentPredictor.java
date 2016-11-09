package mllib.recommender;

import static java.lang.Math.toIntExact;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import mllib.model.DistributedUserItemMatrix;
import mllib.utility.MllibUtilities;

public class SpaceAlignmentPredictor {
	
	DistributedUserItemMatrix R;
	IndexedRowMatrix C;
	int r;
	IndexedRowMatrix Mprime;
	
	public SpaceAlignmentPredictor(DistributedUserItemMatrix R, int r, IndexedRowMatrix C){
		this.R = R;
		this.r = r;
		this.C = C;
		calculateMprime();
	}
	
	private void calculateMprime(){
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd= C.computeSVD(toIntExact(C.numRows()), true, 1.0E-9d);
		IndexedRowMatrix U = Csvd.U();
		Matrix V = Csvd.V();
		Matrix Vt = V.transpose();
		IndexedRowMatrix S = R.getItemSimilarities().toIndexedRowMatrix();
		Vector sigma = Csvd.s();
		Vector invertedSigma = invertVector(sigma);
		IndexedRowMatrix Ut = MllibUtilities.transpose(U);
		//*********************************Intermediate operations*********************************************
		IndexedRowMatrix leftMat = MllibUtilities.multiplicateByLeftDiagonalMatrix(invertedSigma, Ut);
		IndexedRowMatrix rightMat = MllibUtilities.multiplicateByRightDiagonalMatrix(U, Csvd.s());
		IndexedRowMatrix intermediateMat = leftMat.multiply(MllibUtilities.toSparseLocalMatrix(S)).multiply(MllibUtilities.toSparseLocalMatrix(rightMat));
		//***************************************************************************************************
		SingularValueDecomposition<IndexedRowMatrix, Matrix> intMatsvd = intermediateMat.computeSVD(toIntExact(intermediateMat.numRows()), true, 1.0E-9d);
		IndexedRowMatrix Q = intMatsvd.U();
		Vector hardTrhesholdedLambda = MllibUtilities.hardThreshold(intMatsvd.s(),r);
		IndexedRowMatrix QtVt = MllibUtilities.transpose(Q).multiply(Vt);
		IndexedRowMatrix VQ = MllibUtilities.transpose(QtVt);
		Mprime = MllibUtilities.multiplicateByRightDiagonalMatrix(VQ, hardTrhesholdedLambda).multiply(MllibUtilities.toSparseLocalMatrix(QtVt));
	}
	
	public double predictItemsSimilarity(Vector coldStartItemContent, int oldItemIndex){
		Vector targetItem = MllibUtilities.indexedRowToVector(C.rows().toJavaRDD().filter(row -> row.index() == oldItemIndex).collect().get(0));
		return MllibUtilities.scalarProduct(MllibUtilities.multiplyColumnVectorByMatrix(Mprime, coldStartItemContent),targetItem);
	}
	
	private Vector invertVector(Vector v){
		double[] values = v.toArray();
		for(int i = 0; i < values.length; i++){
			values[i] = 1.0d/values[i];
		}
		return Vectors.dense(values);
	}
}
