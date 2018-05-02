package model.measure.similarity;

import static util.spark.mllib.MllibUtilities.calculateL2Norm;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

import model.measure.Measure;

public class NormalizedCosineSimilarity implements Measure{
	
	private static final NormalizedCosineSimilarity instance = new NormalizedCosineSimilarity();
	
	private NormalizedCosineSimilarity() {};
	
	public static NormalizedCosineSimilarity getInstance(){
		return instance;
	}
	
	@Override
	public double calculate(SparseVector i, SparseVector j) {
		double normI = calculateL2Norm(i);
		double normJ = calculateL2Norm(j);
		double dotProduct = CosineSimilarity.instance().calculate(i, j);
		return (normI == 0.0d || normJ == 0.0d) ? 0.0d : dotProduct / (normI * normJ);
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		double normI = calculateL2Norm(i);
		double normJ = calculateL2Norm(j);
		double dotProduct = CosineSimilarity.instance().calculate(i, j);
		return (normI == 0.0d || normJ == 0.0d) ? 0.0d : dotProduct / (normI * normJ);
	}
}
