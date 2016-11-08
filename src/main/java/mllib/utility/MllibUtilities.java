package mllib.utility;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import scala.Tuple2;
import scala.Tuple3;
import scala.annotation.implicitNotFound;

/**
 * Class that offers multiple utility function on mlllib distributed matrix
 * object.
 * 
 * @author Jonathan Bergeron
 *
 */
public class MllibUtilities {

	/**
	 * Method that transposes the given matrix.
	 * 
	 * @param M
	 *            The matrix to be transposed.
	 * @return The transpose of the given matrix.
	 */
	public static IndexedRowMatrix transpose(IndexedRowMatrix M) {
		JavaRDD<IndexedRow> transposedMatrix = M
				.rows()
				.toJavaRDD()
				.<Tuple3<Integer, Integer, Double>> flatMap(
						row -> mapRowToDenseTriplets(row))
				.mapToPair(
						triplet -> new Tuple2<Integer, Tuple2<Integer, Double>>(
								triplet._2(), new Tuple2<Integer, Double>(
										triplet._1(), triplet._3())))
				.aggregateByKey(new ArrayList<Tuple2<Integer, Double>>(),
						(list, tuple) -> {
							list.add(tuple);
							return list;
						}, (list1, list2) -> {
							list1.addAll(list2);
							return list1;
						}).map(data -> mapDataToRow(data));
		return new IndexedRowMatrix(transposedMatrix.rdd());
	}

	/**
	 * Method that inverse a diagonal matrix.
	 * 
	 * @param M
	 *            The diagonal matrix.
	 * @return The inverse of the diagonal matrix.
	 */
	public static IndexedRowMatrix inverseDiagonalMatrix(IndexedRowMatrix M) {
		JavaRDD<IndexedRow> inverse = M.rows().toJavaRDD().map(row -> {
			int exactIndex = toIntExact(row.index());
			double[] data = row.vector().toArray();
			if (exactIndex <= data.length)
				data[exactIndex] = 1.0d / data[exactIndex];
			return new IndexedRow(row.index(), Vectors.dense(data));
		});
		return new IndexedRowMatrix(inverse.rdd());
	}

	/**
	 * Method that applies the hard thresholding operator to a diagonal matrix,
	 * i.e. keeping only the top r eigen value, the rest is set to zero.
	 * IMPORTANT It assumes the eigen values to be in descending order.
	 * 
	 * @param M
	 *            The matrix to hard threshold.
	 * @param r
	 *            The hard threshold.
	 * @return The hard thresholded matrix.
	 */
	public static IndexedRowMatrix hardThreshold(IndexedRowMatrix M, int r) {
		JavaRDD<IndexedRow> hardThresholdedMatrix = M.rows().toJavaRDD()
				.map(row -> {
					int exactIndex = toIntExact(row.index());
					double[] data = row.vector().toArray();
					if (exactIndex < r && exactIndex <= data.length)
						data[exactIndex] = 0.0d;
					return new IndexedRow(row.index(), Vectors.dense(data));
				});

		return new IndexedRowMatrix(hardThresholdedMatrix.rdd());
	}

	/**
	 * Method that applies the hard thresholding operator to a diagonal matrix
	 * represented by a vector. The hard thresholding operation keeps the top r
	 * singular values. IMPORTANT It assumes the eigen values to be in
	 * descending order.
	 * 
	 * @param v
	 *            The diagonal matrix represented by a vector.
	 * @param r
	 *            The hard threshold value.
	 * @return The hard thresholded matrix in vector representation.
	 */
	public static Vector hardThreshold(Vector v, int r) {
		double[] values = v.toArray();
		for (int i = r; i < values.length; i++) {
			values[i] = 0.0d;
		}
		return Vectors.dense(values).compressed();
	}
	
	/**
	 * Method that do the left multiplication of a diagonal matrix to an arbitrary matrix.
	 * Assume the diagonal matrix D is a m x n matrix and A is an arbitrary matrix of size m' x n'.
	 * Then we must have n = m'.
	 * @param diagMatrix The diagonal matrix in vector form.
	 * @param mat The arbitrary matrix.
	 * @return The matrix DA of size m x n'.
	 */
	public static IndexedRowMatrix multiplicateByLeftDiagonalMatrix(
			Vector diagMatrix, IndexedRowMatrix mat) {
		final double[] diagMatValues = diagMatrix.toArray();
		JavaRDD<IndexedRow> result = mat
				.rows()
				.toJavaRDD()
				.map(row -> {
					double[] rowValues = row.vector().toArray();
					for (int i = 0; i < rowValues.length; i++) {
						rowValues[i] = rowValues[i]
								* diagMatValues[toIntExact(row.index())];
					}
					return new IndexedRow(row.index(), Vectors.dense(rowValues));
				});
		return new IndexedRowMatrix(result.rdd());
	}
	
	/**
	 * Method that do the right multiplication of a diagonal matrix to an arbitrary matrix.
	 * Assume the diagonal matrix D is a m x n matrix and A is an arbitrary matrix of size m' x n'.
	 * Then we must have n' = m.
	 * @param mat The arbitrary matrix.
	 * @param diagMatrix The diagonal matrix in vector form.
	 * @return The matrix AD of size m x n'.
	 */
	public static IndexedRowMatrix multiplicateByRightDiagonalMatrix(
			IndexedRowMatrix mat, Vector diagMatrix) {
		final double[] diagMatValues = diagMatrix.toArray();
		JavaRDD<IndexedRow> result = mat.rows().toJavaRDD().map(row -> {
			double[] rowValues = row.vector().toArray();
			for (int i = 0; i < rowValues.length; i++) {
				rowValues[i] = rowValues[i] * diagMatValues[i];
			}
			return new IndexedRow(row.index(), Vectors.dense(rowValues));
		});
		return new IndexedRowMatrix(result.rdd());
	}
	
	/**
	 * Method that performs the scalar product between two double arrays.
	 * They must be the same size.
	 * @param v1 The first array of double.
	 * @param v2 The second array of double
	 * @return The scalar product between the arrays.
	 */
	public static double scalarProduct(Vector v1, Vector v2) {
		double[] v1Values = v1.toArray();
		double[] v2Values = v2.toArray();
		double total = 0;
		for (int i = 0; i < v1Values.length; i++) {
			total += v1Values[i] * v2Values[i];
		}
		return total;
	}
	
	/**
	 * Method that transforms an IndexedRowMatrix into a local Matrix.
	 * @param mat The matrix in distributed form.
	 * @return The matrix in local form.
	 */
	public static Matrix toSparseLocalMatrix(IndexedRowMatrix mat) {
		ArrayList<Tuple3<Integer, Integer, Double>> triplets = new ArrayList<Tuple3<Integer, Integer, Double>>();
		triplets.addAll(mat.rows()
				.toJavaRDD().flatMap(row -> mapRowToSparseTriplets(row))
				.collect());
		sortTripletsByColumn(triplets);
		int[] rowIndices = new int[triplets.size()];
		int[] colIndices = new int[triplets.size()];
		double[] values = new double[triplets.size()];
		for (int i = 0; i < triplets.size(); i++) {
			rowIndices[i] = triplets.get(i)._1();
			colIndices[i] = triplets.get(i)._2();
			values[i] = triplets.get(i)._3();
		}
		int numRow = toIntExact(mat.numRows());
		int numCol = toIntExact(mat.numCols());
		int[] colPtrs = getColPtrsFromColIndices(colIndices, numCol);
		return Matrices.sparse(numRow,
				numCol, colPtrs, rowIndices, values);
	}

	public static Vector multiplyVectorByMatrix(Vector vec, IndexedRowMatrix mat) {
		List<Double> results = mat
				.rows()
				.toJavaRDD()
				.mapToDouble(
						row -> scalarProduct(vec, row.vector()))
				.collect();
		return Vectors.dense(
				ArrayUtils.toPrimitive(results.toArray(new Double[results
						.size()]))).compressed();
	}

	public static Vector indexedRowToVector(IndexedRow row) {
		return Vectors.dense(row.vector().toArray());
	}
	
	private static void sortTripletsByColumn(List<Tuple3<Integer, Integer, Double>> triplets){
		Collections.sort(triplets, new Comparator<Tuple3<Integer, Integer, Double>>() {

			@Override
			public int compare(Tuple3<Integer, Integer, Double> triplet1, Tuple3<Integer, Integer, Double> triplet2) {
				return triplet1._2().compareTo(triplet2._2());
			}
		});
	}
	
	private static int[] getColPtrsFromColIndices(int[] colIndices, int numCol){
		int[] numberOfNonZerosPerColumn = new int[numCol];
		for(int colIndice : colIndices){
			numberOfNonZerosPerColumn[colIndice]++;
		}
		int[] colPtrs = new int[numCol+1];
		colPtrs[0] = 0;
		for(int i = 1; i < numCol+1 ; i++){
			colPtrs[i] = colPtrs[i-1] + numberOfNonZerosPerColumn[i-1];
		}
		return colPtrs;
	}

	private static IndexedRow mapDataToRow(
			Tuple2<Integer, ArrayList<Tuple2<Integer, Double>>> data) {
		int rowSize = data._2.size();
		double[] rowValues = new double[rowSize];
		for (int i = 0; i < rowSize; i++) {
			rowValues[data._2.get(i)._1] = data._2.get(i)._2;
		}
		return new IndexedRow(data._1, Vectors.dense(rowValues).compressed());
	}

	private static Iterator<Tuple3<Integer, Integer, Double>> mapRowToSparseTriplets(
			IndexedRow row) {
		List<Tuple3<Integer, Integer, Double>> triplets = new ArrayList<Tuple3<Integer, Integer, Double>>();
		int rowIndex = toIntExact(row.index());
		SparseVector sparceRow = row.vector().toSparse();
		int[] indices = sparceRow.indices();
		double[] values = sparceRow.values();
		for (int i = 0; i < indices.length; i++) {
			triplets.add(new Tuple3<Integer, Integer, Double>(rowIndex,
					indices[i], values[i]));
		}
		return triplets.iterator();
	}

	private static Iterator<Tuple3<Integer, Integer, Double>> mapRowToDenseTriplets(
			IndexedRow row) {
		List<Tuple3<Integer, Integer, Double>> values = new ArrayList<Tuple3<Integer, Integer, Double>>();
		double[] colValues = row.vector().toArray();
		for (int col = 0; col < colValues.length; col++) {
			values.add(new Tuple3<Integer, Integer, Double>(toIntExact(row
					.index()), col, colValues[col]));
		}
		return values.iterator();
	}
}
