package model.similarity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;

public class CosineSimilarity implements SimilarityMeasure{
	
	private static final CosineSimilarity instance = new CosineSimilarity();
	
	private CosineSimilarity(){};
	
	public static CosineSimilarity getInstance(){
		return instance;
	}
	
	@Override
	public double calculateSimilarity(SparseVector i, SparseVector j) {
		List<Integer> indicesListI = Arrays.asList(ArrayUtils.toObject(i.indices()));
		List<Integer> indicesListJ = Arrays.asList(ArrayUtils.toObject(j.indices()));
		Set<Integer> indices = new HashSet<Integer>(indicesListI);
		Set<Integer> indicesJ = new HashSet<Integer>(indicesListJ);
		indices.retainAll(indicesJ);
		return indices.stream().mapToDouble(index -> i.apply(index) * j.apply(index)).sum();
		
		
	}

	@Override
	public double calculateSimilarity(DenseVector i, DenseVector j) {
		return IntStream.range(0, Math.min(i.size(), j.size())).mapToDouble(index -> i.apply(index) * j.apply(index)).sum();
	}
	
}
