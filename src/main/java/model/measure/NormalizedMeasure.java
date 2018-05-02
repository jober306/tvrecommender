package model.measure;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import util.spark.mllib.MllibUtilities;

public class NormalizedMeasure implements Measure{
	
	final Measure measure;
	
	public NormalizedMeasure(Measure measure) {
		this.measure = measure;
	}
	
	@Override
	public double calculate(SparseVector i, SparseVector j) {
		SparseVector normalizedI = MllibUtilities.normalize(i);
		SparseVector normalizedJ = MllibUtilities.normalize(j);
		return measure.calculate(normalizedI, normalizedJ);
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		DenseVector normalizedI = MllibUtilities.normalize(i);
		DenseVector normalizedJ = MllibUtilities.normalize(j);
		return measure.calculate(normalizedI, normalizedJ);
	}

}
