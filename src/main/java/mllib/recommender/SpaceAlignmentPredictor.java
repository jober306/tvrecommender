package mllib.recommender;

import static java.lang.Math.toIntExact;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.DistributedMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import mllib.model.DistributedUserItemMatrix;
import spark.utilities.DistributedMatrixUtilities;

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
		IndexedRowMatrix Ut = DistributedMatrixUtilities.transpose(U);
		//*********************************Intermediate operations*********************************************
		IndexedRowMatrix leftMat = DistributedMatrixUtilities.multiplicateByLeftDiagonalMatrix(invertedSigma, Ut);
		IndexedRowMatrix rightMat = DistributedMatrixUtilities.multiplicateByRightDiagonalMatrix(U, Csvd.s());
		IndexedRowMatrix intermediateMat = leftMat.multiply(DistributedMatrixUtilities.toSparseLocalMatrix(S)).multiply(DistributedMatrixUtilities.toSparseLocalMatrix(rightMat));
		//***************************************************************************************************
		SingularValueDecomposition<IndexedRowMatrix, Matrix> intMatsvd = intermediateMat.computeSVD(toIntExact(intermediateMat.numRows()), true, 1.0E-9d);
		IndexedRowMatrix Q = intMatsvd.U();
		Vector hardTrhesholdedLambda = DistributedMatrixUtilities.hardThreshold(intMatsvd.s(),r);
		IndexedRowMatrix QtVt = DistributedMatrixUtilities.transpose(Q).multiply(Vt);
		IndexedRowMatrix VQ = DistributedMatrixUtilities.transpose(QtVt);
		Mprime = DistributedMatrixUtilities.multiplicateByRightDiagonalMatrix(VQ, hardTrhesholdedLambda).multiply(DistributedMatrixUtilities.toSparseLocalMatrix(QtVt));
	}
	
	private Vector invertVector(Vector v){
		double[] values = v.toArray();
		for(int i = 0; i < values.length; i++){
			values[i] = 1.0d/values[i];
		}
		return Vectors.dense(values);
	}
}
