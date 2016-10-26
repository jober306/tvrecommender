package mllib.model;

import java.util.List;

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
	
	public IndexedRow getRow(int rowIndex){
		List<IndexedRow> rows =  data.rows().toJavaRDD().filter(indexedRow -> indexedRow.index() == rowIndex).collect();
		if(rows.size() == 1){
			return rows.get(0);
		}
		return null;
	}
	
	public double getValue(int rowIndex, int columnIndex){
		return getRow(rowIndex).vector().toArray()[columnIndex];
	}
	
	/**
	 * Wrapper method that return cosine similarity between columns.
	 * @return A coordinate matrix containing similarities between columns.
	 */
	public CoordinateMatrix getItemSimilarities(){
		return data.columnSimilarities();
	}
}
