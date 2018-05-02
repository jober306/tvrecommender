package model.measure;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;

public interface Measure {
	
	public double calculate(SparseVector i, SparseVector j);
	public double calculate(DenseVector i, DenseVector j);
	
	public default double calculate(Vector i, Vector j){
		if(i instanceof DenseVector && j instanceof DenseVector){
			DenseVector sparseI = (DenseVector) i;
			DenseVector sparseJ = (DenseVector) j;
			return calculate(sparseI, sparseJ);
		}
		else{
			SparseVector denseI = i.toSparse();
			SparseVector denseJ = j.toSparse();
			return calculate(denseI, denseJ);
		}
	}
}
