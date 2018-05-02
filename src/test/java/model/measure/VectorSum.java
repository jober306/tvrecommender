package model.measure;

import java.util.Arrays;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

public class VectorSum implements Measure{
	
	private static VectorSum INSTANCE = new VectorSum();
	
	private VectorSum(){};
	
	public static VectorSum instance(){
		return INSTANCE;
	}
	
	@Override
	public double calculate(SparseVector i, SparseVector j) {
		return calculate(i.values(), j.values());
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		return calculate(i.values(), j.values());
	}
	
	private double calculate(double[] iValues, double[] jValues){
		double iSum = Arrays.stream(iValues).sum();
		double jSum = Arrays.stream(jValues).sum();
		return iSum + jSum;
	}
}
