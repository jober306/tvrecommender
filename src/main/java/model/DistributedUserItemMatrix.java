package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.similarity.SimilarityMeasure;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;

import scala.Tuple3;
import util.MllibUtilities;
import util.SparkUtilities;

import com.google.common.primitives.Ints;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

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
	 * Constructor of the class.
	 * 
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
	public List<Integer> getItemIndexesSeenByUser(int userIndex) {
		if(userIndex > data.numRows()){
			return new ArrayList<Integer>();
		}
		return Ints.asList(getRow(userIndex).toSparse().indices());
	}
	
	public CoordinateMatrix getItemSimilarities(JavaSparkContext sc){
		JavaRDD<MatrixEntry> upperTriangEntries =  data.columnSimilarities().entries().toJavaRDD();
		JavaRDD<MatrixEntry> simMatrixEntries = upperTriangEntries.flatMap(entry -> {
			long i = entry.i();
			long j = entry.j();
			if(i != j){
				MatrixEntry symetricEntry = new MatrixEntry(j, i, entry.value());
				return Arrays.asList(entry, symetricEntry).iterator();
			}else{
				return Arrays.asList(entry).iterator();
			}
		});
		List<MatrixEntry> diagonalEntries = new ArrayList<MatrixEntry>();
		for(int col = 0; col < getNumCols(); col++){
			diagonalEntries.add(new MatrixEntry(col, col, 1.0d));
		}
		simMatrixEntries = SparkUtilities.elementsToJavaRDD(diagonalEntries, sc).union(simMatrixEntries);
		return new CoordinateMatrix(simMatrixEntries.rdd());
	}
	
	/**
	 * Wrapper method that return cosine similarity between columns.
	 * 
	 * @param simMeasure TODO: use the similarity measure if one is given. Only use column similarities from mllib
	 * if instance of <Class>NormallizedCosineSimilarity</class> is given.
	 * 
	 * @return A coordinate matrix containing similarities between columns.
	 */
	public Matrix getItemSimilarities(SimilarityMeasure simMeasure) {
		int numCols = (int) getNumCols();
		/*The upper triangular item similarity matrix calculated by mllib*/
		CoordinateMatrix simMat = data.columnSimilarities();
		/*Adding all the missing values*/
		List<MatrixEntry> allEntries = new ArrayList<MatrixEntry>();
		List<MatrixEntry> upperAndLowerMat = simMat.entries().toJavaRDD().flatMap(
						entry -> {
							List<MatrixEntry> entries = new ArrayList<MatrixEntry>();
							entries.add(entry);
							if (entry.i() != entry.j()) {
								entries.add(new MatrixEntry(entry.j(), entry
										.i(), entry.value()));
							}
							return entries.iterator();
						}).collect();
		List<MatrixEntry> diagonalEntries = new ArrayList<MatrixEntry>();
		for(int col = 0; col < numCols; col++){
			diagonalEntries.add(new MatrixEntry(col, col, 1.0d));
		}
		allEntries.addAll(upperAndLowerMat);
		allEntries.addAll(diagonalEntries);
		Tuple3<int[], int[], double[]> matrixData = MllibUtilities.sparseMatrixFormatToCSCMatrixFormat(numCols, allEntries);
		return Matrices.sparse(numCols, numCols, matrixData._1(), matrixData._2(), matrixData._3());
	}
}
