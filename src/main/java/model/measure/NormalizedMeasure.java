package model.measure;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import util.spark.mllib.MllibUtilities;

/**
 * Abstract class that represents the normalized version of a given measure.
 * This class normalizes the vectors before calculating the given measure.
 * @author Jonathan Bergeron
 *
 */
public abstract class NormalizedMeasure implements Measure{
	
	final Measure measure;
	
	/**
	 * Abstract constructor of this class.
	 * @param measure The measure to normalize when calculating it.
	 */
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
