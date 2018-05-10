package model.measure.distance;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import model.data.TVProgram;
import model.data.User;
import util.spark.mllib.MllibUtilities;

public class EuclideanDistance<U extends User, P extends TVProgram> implements DistanceMeasure {

	@Override
	public double calculate(SparseVector i, SparseVector j) {
		return MllibUtilities.calculateL2Norm(MllibUtilities.subtract(i, j));
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		return MllibUtilities.calculateL2Norm(MllibUtilities.subtract(i, j));
	}
	
	
}
