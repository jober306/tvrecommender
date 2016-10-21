package mllib.model;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;


public class DistributedUserItemMatrix {
	
	
	IndexedRowMatrix data;
	
	/**
	 * Constructor of the class. Matrix is initialized with zeros.
	 * 
	 * @param numberOfUsers
	 *            The number of distinct users.
	 * @param numberOfItems
	 *            The number of distinct items.
	 */
	public DistributedUserItemMatrix(JavaRDD<IndexedRow> rows) {
		data = new IndexedRowMatrix(rows.rdd());
	}
	
	public CoordinateMatrix getItemSimilarities(){
		return data.columnSimilarities();
	}
}
