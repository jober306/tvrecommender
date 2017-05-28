package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;

/**
 * Class that represents an user item matrix (or rating matrix) in a mllib
 * distributed representation.
 * 
 * @author Jonathan Bergeron
 *
 */
public class DistributedUserItemMatrix extends UserItemMatrix implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -135750142194246162L;
	
	/**
	 * The mllib distributed matrix.
	 */
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

	/**
	 * Method that returns the value at specified row index and column index.
	 * 
	 * @param rowIndex
	 *            The row index in the matrix from 0 to numRow-1.
	 * @param columnIndex
	 *            The col index in the matrix from 0 to numCol -1.
	 * @return The value at the specified row index and column index.
	 */
	public double getValue(int rowIndex, int columnIndex) {
		return getRow(rowIndex).toArray()[columnIndex];
	}

	/**
	 * Wrapper method that returns the number of rows in the user item matrix.
	 * 
	 * @return The number of rows.
	 */
	public long getNumRows() {
		return data.numRows();
	}

	/**
	 * Wrapper method that returns the number of cols in the user item matrix.
	 * 
	 * @return The number of cols.
	 */
	public long getNumCols() {
		return data.numCols();
	}

	/**
	 * Method that returns the row of the specified index
	 * 
	 * @param rowIndex
	 *            The row index in the matrix from 0 to numRow-1.
	 * @return The Indexed Row corresponding to rowIndex.
	 */
	public Vector getRow(int rowIndex) {
		List<IndexedRow> rows = data.rows().toJavaRDD()
				.filter(indexedRow -> indexedRow.index() == rowIndex).collect();
		if (rows.size() == 1) {
			return rows.get(0).vector();
		}
		return null;
	}

	/**
	 * Method that returns all the item indexes seen by a specific user.
	 * 
	 * @param userIndex
	 *            The index of the user in the user item matrix.
	 * @return The array of item indexes seen by the user.
	 */
	public int[] getItemIndexesSeenByUser(int userIndex) {
		return getRow(userIndex).toSparse().indices();
	}

	/**
	 * Wrapper method that return cosine similarity between columns. TODO: It
	 * may be not necessary to complete the symmetric matrix.
	 * 
	 * @return A coordinate matrix containing similarities between columns.
	 */
	public CoordinateMatrix getItemSimilarities() {
		JavaRDD<MatrixEntry> simMat = data
				.columnSimilarities()
				.entries()
				.toJavaRDD()
				.flatMap(
						entry -> {
							List<MatrixEntry> entries = new ArrayList<MatrixEntry>();
							entries.add(entry);
							if (entry.i() != entry.j()) {
								entries.add(new MatrixEntry(entry.j(), entry
										.i(), entry.value()));
							}
							return entries.iterator();
						});
		CoordinateMatrix fullSpecifiedItemSim = new CoordinateMatrix(
				simMat.rdd(), data.numCols(), data.numCols());
		return fullSpecifiedItemSim;
	}
}
