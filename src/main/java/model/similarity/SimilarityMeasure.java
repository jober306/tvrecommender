package model.similarity;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;

public interface SimilarityMeasure {
	
	public double calculateSimilarity(SparseVector i, SparseVector j);
	public double calculateSimilarity(DenseVector i, DenseVector j);
	
	public default double calculateSimilarity(Vector i, Vector j){
		if(i instanceof SparseVector && j instanceof SparseVector){
			SparseVector sparseI = (SparseVector) i;
			SparseVector sparseJ = (SparseVector) j;
			return calculateSimilarity(sparseI, sparseJ);
		}
		else{
			DenseVector denseI = i.toDense();
			DenseVector denseJ = j.toDense();
			return calculateSimilarity(denseI, denseJ);
		}
	}
}
