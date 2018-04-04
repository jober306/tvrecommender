package model.similarity;

import static util.spark.mllib.MllibUtilities.calculateL2Norm;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

public class NormalizedCosineSimilarity implements SimilarityMeasure{
	
	private static final NormalizedCosineSimilarity instance = new NormalizedCosineSimilarity();
	
	private NormalizedCosineSimilarity() {};
	
	public static NormalizedCosineSimilarity getInstance(){
		return instance;
	}
	
	@Override
	public double calculateSimilarity(SparseVector i, SparseVector j) {
		double normI = calculateL2Norm(i);
		double normJ = calculateL2Norm(j);
		double dotProduct = CosineSimilarity.getInstance().calculateSimilarity(i, j);
		return dotProduct / (normI * normJ);
	}

	@Override
	public double calculateSimilarity(DenseVector i, DenseVector j) {
		double normI = calculateL2Norm(i);
		double normJ = calculateL2Norm(j);
		double dotProduct = CosineSimilarity.getInstance().calculateSimilarity(i, j);
		return dotProduct / (normI * normJ);
	}
}
