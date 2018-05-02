package model.measure;

import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;

/**
 * An interface defining a measure between two mllib vectors. It must be able 
 * to calculate the measure for the two types of mllib vectors: dense and sparse representation.
 * @author Jonathan Bergeron
 *
 */
public interface Measure {
	
	/**
	 * Method that calculates the measure between two sparse vectors.
	 * @param i The first sparse vector.
	 * @param j The second sparse vector.
	 * @return The measure result.
	 */
	public double calculate(SparseVector i, SparseVector j);
	
	/**
	 * Method that calculates the measure between two dense vectors.
	 * @param i The first dense vector.
	 * @param j The second dense vector.
	 * @return The measure result.
	 */
	public double calculate(DenseVector i, DenseVector j);
	
	/**
	 * Method that calculates the measure between two vectors.
	 * If both vectors are not dense they are converted to sparse vectors
	 * to calculate the measure.
	 * @param i The first vector.
	 * @param j The second vector.
	 * @return The measure result.
	 */
	public default double calculate(Vector i, Vector j){
		if(i instanceof DenseVector && j instanceof DenseVector){
			DenseVector denseI = (DenseVector) i;
			DenseVector denseJ = (DenseVector) j;
			return calculate(denseI, denseJ);
		}
		else{
			SparseVector sparseI = i.toSparse();
			SparseVector sparseJ = j.toSparse();
			return calculate(sparseI, sparseJ);
		}
	}
}
