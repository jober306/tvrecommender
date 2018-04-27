package model.similarity;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;

public interface SimilarityMeasure {
	
	public double calculateSimilarity(SparseVector i, SparseVector j);
	public double calculateSimilarity(DenseVector i, DenseVector j);
	
	public default double calculateSimilarity(Vector i, Vector j){
		if(i instanceof DenseVector && j instanceof DenseVector){
			DenseVector sparseI = (DenseVector) i;
			DenseVector sparseJ = (DenseVector) j;
			return calculateSimilarity(sparseI, sparseJ);
		}
		else{
			SparseVector denseI = i.toSparse();
			SparseVector denseJ = j.toSparse();
			return calculateSimilarity(denseI, denseJ);
		}
	}
}
