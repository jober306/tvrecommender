package model;

import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;

import model.similarity.SimilarityMeasure;
import scala.Tuple3;
import scala.collection.Iterator;
import util.MllibUtilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.mllib.linalg.Matrices;

/**
 * Class that wraps the local matrix of mllib to represent an user-item matrix.
 * It offers multiple utilities methods.
 * @author Jonathan Bergeron
 *
 */
public class LocalUserItemMatrix extends UserItemMatrix implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6401494865309223613L;
	
	/**
	 * The mllib user item matrix.
	 */
	Matrix R;
	
	/**
	 * Construct a dense matrix with numRow and numCol with all the specified values.
	 * @param numRow The matrix number of row.
	 * @param numCol The matrix number of column.
	 * @param values The values of the matrix in column major order.
	 */
	public LocalUserItemMatrix(int numRow, int numCol, double[] values){
		this.R = Matrices.dense(numRow, numCol, values);
	}
	

	/**
	 * Construct a sparse matrix with numRow and numCol with all the specified values. 
	 * The values are stored in Compressed Sparse Column (CSC) format. For example, the following matrix

									   1.0 0.0 4.0
									   0.0 3.0 5.0
									   2.0 0.0 6.0
 
	 * is stored as values: [1.0, 2.0, 3.0, 4.0, 5.0, 6.0], rowIndices=[0, 2, 1, 0, 1, 2], colPointers=[0, 2, 3, 6].
	 * @param numRow
	 * @param numCol
	 * @param colPtrs
	 * @param rowIndices
	 * @param values
	 */
	public LocalUserItemMatrix(int numRow, int numCol, int[] colPtrs, int[] rowIndices, double[] values){
		this.R = Matrices.sparse(numRow, numCol, colPtrs, rowIndices, values);
	}
	
	/**
	 * Wrapper method that returns the number of rows in the user item matrix.
	 * 
	 * @return The number of rows.
	 */
	public long getNumRows() {
		return R.numRows();
	}

	/**
	 * Wrapper method that returns the number of cols in the user item matrix.
	 * 
	 * @return The number of cols.
	 */
	public long getNumCols() {
		return (int)R.numCols();
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
		return R.apply(rowIndex, columnIndex);
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
	 * Method that returns the row of the specified index
	 * 
	 * @param rowIndex
	 *            The row index in the matrix from 0 to numRow-1.
	 * @return The Indexed Row corresponding to rowIndex.
	 */
	public Vector getRow(int rowIndex) {
		Iterator<Vector> rows = R.rowIter();
		int currentRow = 0;
		while(currentRow != rowIndex && rows.hasNext()){
			rows.next();
			currentRow++;
		}
		return rows.next();
	}
	
	/**
	 * Wrapper method that return cosine similarity between columns. TODO: It
	 * may be not necessary to complete the symmetric matrix.
	 * 
	 * @param simMeasure The similarity measure used to calculate similarity between items (tv shows).
	 * 
	 * @return A sparse matrix containing the similarity between each item.
	 */
	public Matrix getItemSimilarities(SimilarityMeasure simMeasure) {
		Map<Integer, Vector> vectors = createColumnVectorMap();
		List<MatrixEntry> similarities = new ArrayList<MatrixEntry>();
		for(int row = 0; row < R.numRows()-1; row++){
			for(int col = row+1; col < R.numCols(); col++){
				double sim = simMeasure.calculateSimilarity(vectors.get(row), vectors.get(col));
				if(sim != 0){
					similarities.add(new MatrixEntry(row, col, sim));
				}
			}
		}
		/*Adding all the missing values*/
		List<MatrixEntry> allEntries = similarities.stream().flatMap(
						entry -> {
							List<MatrixEntry> entries = new ArrayList<MatrixEntry>();
							entries.add(entry);
							if (entry.i() != entry.j()) {
								entries.add(new MatrixEntry(entry.j(), entry
										.i(), entry.value()));
							}
							return entries.stream();
						}).collect(Collectors.toList());
		List<MatrixEntry> diagonalEntries = new ArrayList<MatrixEntry>();
		for(int col = 0; col < getNumCols(); col++){
			diagonalEntries.add(new MatrixEntry(col, col, 1.0d));
		}
		allEntries.addAll(diagonalEntries);
		Tuple3<int[], int[], double[]> matrixData = MllibUtilities.sparseMatrixFormatToCSCMatrixFormat((int) getNumCols(), allEntries);
		return Matrices.sparse((int)getNumCols(), (int)getNumCols(), matrixData._1(), matrixData._2(), matrixData._3());
	}
	
	private Map<Integer, Vector> createColumnVectorMap(){
		Map<Integer, Vector> vectors = new HashMap<Integer, Vector>();
		Iterator<Vector> it = R.colIter();
		int index = 0;
		while(it.hasNext()){
			vectors.put(index, it.next());
			index++;
		}
		return vectors;
	}
}
