package model.measure.distance;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import model.measure.Measure;
import model.measure.similarity.SimilarityMeasure;

public abstract class DistanceMeasure implements Measure{
	
	final SimilarityMeasure similarityMeasure;
	
	public DistanceMeasure(SimilarityMeasure similarityMeasure){
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
