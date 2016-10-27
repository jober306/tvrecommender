package mllib.recommender;

import static java.lang.Math.toIntExact;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import mllib.model.DistributedUserItemMatrix;
import spark.utilities.DistributedMatrixUtilities;

public class SpaceAlignmentPredictor {
	
	DistributedUserItemMatrix R;
	int r;
	IndexedRowMatrix Mprime;
	
	public SpaceAlignmentPredictor(DistributedUserItemMatrix R, int r){
		this.R = R;
		this.r = r;
	}
	
	private void calculateMprime(IndexedRowMatrix C){
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd= C.computeSVD(toIntExact(C.numRows()), true, 1.0E-9d);
		IndexedRowMatrix U = Csvd.U();
		CoordinateMatrix S = R.getItemSimilarities();
		Vector invertedS = invertVector(Csvd.s());
		IndexedRowMatrix Ut = DistributedMatrixUtilities.transpose(U);
		Ut.mu
	}
	
	private Vector invertVector(Vector v){
		double[] values = v.toArray();
		for(int i = 0; i < values.length; i++){
			values[i] = 1.0d/values[i];
		}
		return Vectors.dense(values);
	}
}
