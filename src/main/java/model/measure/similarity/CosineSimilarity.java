package model.measure.similarity;

import java.util.Set;
import java.util.stream.IntStream;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.spark_project.guava.primitives.Ints;

import com.google.common.collect.Sets;

import util.spark.mllib.MllibUtilities;

/**
 * Singleton class used to calculate cosine similarity.
 * @author Jonathan Bergeron
 *
 */
public class CosineSimilarity implements SimilarityMeasure{
	
	private static final CosineSimilarity instance = new CosineSimilarity();
	
	private CosineSimilarity(){};
	
	public static CosineSimilarity instance(){
		return instance;
	}
	
	@Override
	public double calculate(SparseVector i, SparseVector j) {
		Set<Integer> indicesI = Sets.newHashSet(Ints.asList(i.indices()));
		Set<Integer> indicesJ = Sets.newHashSet(Ints.asList(j.indices()));
		indicesI.retainAll(indicesJ);
		double dotProduct = indicesI.stream().mapToDouble(index -> i.apply(index) * j.apply(index)).sum();
		double normI = MllibUtilities.calculateL2Norm(i);
		double normJ = MllibUtilities.calculateL2Norm(j);
		return calculate(dotProduct, normI, normJ);
	}

	@Override
	public double calculate(DenseVector i, DenseVector j) {
		double dotProduct = IntStream.range(0, Math.min(i.size(), j.size())).mapToDouble(index -> i.apply(index) * j.apply(index)).sum();
		double normI = MllibUtilities.calculateL2Norm(i);
		double normJ = MllibUtilities.calculateL2Norm(j);
		return calculate(dotProduct, normI, normJ);
	}
	
	private double calculate(double dotProduct, double normI, double normJ){
		return (normI == 0.0d || normJ == 0.0d) ? 0.0d : dotProduct / (normI * normJ);
	}
}
