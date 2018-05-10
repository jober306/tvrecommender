package model.measure.distance;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import model.measure.similarity.SimilarityMeasure;

/**
 * Class that calculates a distance measure using a similarity measure.
 * It simply substract from 1 the result of the similarity measure. 
 * @author Jonathan Bergeron
 *
 */
public class InversedSimilarityMeasure implements DistanceMeasure{
	
	final SimilarityMeasure similarityMeasure;
	
	public InversedSimilarityMeasure(SimilarityMeasure similarityMeasure){
		this.similarityMeasure = similarityMeasure;
	}
	
	@Override
	public double calculate(SparseVector i, SparseVector j) {
		return 1.0d - similarityMeasure.calculate(i, j);
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		return 1.0d - similarityMeasure.calculate(i, j);
	}

}
