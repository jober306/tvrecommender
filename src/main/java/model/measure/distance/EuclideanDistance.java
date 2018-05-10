package model.measure.distance;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import util.spark.mllib.MllibUtilities;

public class EuclideanDistance implements DistanceMeasure {
	
	static EuclideanDistance INSTANCE = new EuclideanDistance();
	
	private EuclideanDistance() {}
	
	public static EuclideanDistance instance() {
		return INSTANCE;
	}
	
	@Override
	public double calculate(SparseVector i, SparseVector j) {
		return MllibUtilities.calculateL2Norm(MllibUtilities.subtract(i, j));
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		return MllibUtilities.calculateL2Norm(MllibUtilities.subtract(i, j));
	}
	
	
}
