package mllib.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;


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
	
	public long getNumRows(){
		return data.numRows();
	}
	
	public long getNumCols(){
		return data.numCols();
	}
	
	/**
	 * Wrapper method that return cosine similarity between columns.
	 * @return A coordinate matrix containing similarities between columns.
	 */
	public CoordinateMatrix getItemSimilarities(){
		JavaRDD<MatrixEntry> simMat = data.columnSimilarities().entries().toJavaRDD().flatMap(entry -> {
			List<MatrixEntry> entries = new ArrayList<MatrixEntry>();
			entries.add(entry);
			if(entry.i() != entry.j())
			{
				entries.add(new MatrixEntry(entry.j(), entry.i(), entry.value()));
			}
			return entries.iterator();
		});
		CoordinateMatrix fullSpecifiedItemSim = new CoordinateMatrix(simMat.rdd(), data.numCols(), data.numCols());
		return fullSpecifiedItemSim;
	}
}
