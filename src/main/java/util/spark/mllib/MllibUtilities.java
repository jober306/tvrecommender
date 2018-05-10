package util.spark.mllib;

import static java.lang.Math.toIntExact;
import static util.collections.ListUtilities.getSecondArgumentAsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SparseMatrix;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.spark_project.guava.primitives.Doubles;
import org.spark_project.guava.primitives.Ints;

import scala.Tuple2;
import scala.Tuple3;
import util.spark.SparkUtilities;

/**
 * Class that offers multiple utility function on mlllib distributed matrix
 * object.
 * 
 * @author Jonathan Bergeron
 *
 */
public class MllibUtilities {
	
	/**
	 * Method that subtracts the second dense vector to the first one.
	 * @param i The first dense vector.
	 * @param j The second dense vector.
	 * @return The substraction of the first dense vector by the second one.
	 */
	public static DenseVector subtract(DenseVector i, DenseVector j) {
		int resultSize = Math.min(i.size(), j.size());
		double[] iValues = i.values();
		double[] jValues = j.values();
		double[] resultValues = new double[resultSize];
		for(int index = 0; index < resultSize; index++) {
			resultValues[index] = iValues[index] - jValues[index];
		}
		return new DenseVector(resultValues);
	}
	
	/**
	 * Method that subtracts the second sparse vector to the first one.
	 * @param i The first sparse vector.
	 * @param j The second sparse vector.
	 * @return The substraction of the first sparse vector by the second one.
	 */
	public static SparseVector subtract(SparseVector i, SparseVector j) {
		int iIndex = 0;
		int jIndex = 0;
		int[] iIndices = i.indices();
		int[] jIndices = j.indices();
		double[] iValues = i.values();
		double[] jValues = j.values();
		List<Integer> indices = new ArrayList<Integer>();
		List<Double> values = new ArrayList<Double>();
		while(iIndex < iIndices.length && jIndex < jIndices.length) {
			if(iIndices[iIndex] < jIndices[jIndex]) {
				indices.add(iIndices[iIndex]);
				values.add(iValues[iIndex]);
				iIndex++;
			}else if(iIndices[iIndex] > jIndices[jIndex]) {
				indices.add(jIndices[jIndex]);
				values.add(-1.0d * jValues[jIndex]);
				jIndex++;
			}
			else {
				indices.add(iIndices[iIndex]);
				values.add(iValues[iIndex] - jValues[jIndex]);
				iIndex++;
				jIndex++;
			}
		}
		while(iIndex < iIndices.length) {
			indices.add(iIndices[iIndex]);
			values.add(iValues[iIndex]);
			iIndex++;
		}
		while(jIndex < jIndices.length) {
			indices.add(jIndices[jIndex]);
			values.add(-1.0d * jValues[jIndex]);
			jIndex++;
		}
		return new SparseVector(Math.min(i.size(), j.size()), Ints.toArray(indices), Doubles.toArray(values));
	}
	
	/**
	 * Method that takes a dense vector as input and return a normalized deep copy of it.
	 * @param v The dense vector.
	 * @return A normalized and dense deep copy of the given vector.
	 */
	public static DenseVector normalize(DenseVector v){
		double norm = calculateL2Norm(v);
		if(norm == 0.0d){
			return v;
		}
		double[] values = v.values();
		double[] normalizedValues = new double[v.size()];
		for(int index = 0; index < v.size(); index++){
			normalizedValues[index] = values[index] / norm;
		}
		return new DenseVector(normalizedValues);
	}
	
	/**
	 * Method that takes a sparse vector as input and return a normalized deep copy of it.
	 * @param v The sparse vector.
	 * @return A normalized and sparse deep copy of the given vector.
	 */
	public static SparseVector normalize(SparseVector v){
		double norm = calculateL2Norm(v);
		if(norm == 0.0d){
			return v;
		}
		double[] values = v.values();
		int[] indices = v.indices();
		double[] normalizedValues = new double[values.length];
		for(int index = 0; index < indices.length; index++){
			normalizedValues[index] = values[index] / norm;
		}
		return new SparseVector(v.size(), indices, normalizedValues);
	}
	
	/**
	 * Method that returns the euclidean norm of the given vector.
	 * @param v A vector in dense representation.
	 * @return The eucledean norm of the vector v.
	 */
	public static double calculateL2Norm(DenseVector v){
		return Math.sqrt(Arrays.stream(v.values()).map(value -> value * value).sum());
	}
	
	/**
	 * Method that returns the euclidean norm of the given vector.
	 * @param v A vector in sparse representation.
	 * @return The eucledean norm of the vector v.
	 */
	public static double calculateL2Norm(SparseVector v){
		return Math.sqrt(Arrays.stream(v.values()).map(value -> value * value).sum());
	}
	
	/**
	 * Method that transposes the given matrix.
	 * 
	 * @param M
	 *            The matrix to be transposed.
	 * @return The transpose of the given matrix.
	 */
	public static IndexedRowMatrix transpose(IndexedRowMatrix M) {
		JavaRDD<Tuple3<Integer, Integer, Double>> denseMatrix = M.rows()
				.toJavaRDD().flatMap(row -> mapRowToDenseTriplets(row));
		JavaPairRDD<Integer, Tuple2<Integer, Double>> denseMatrixRowColInverted = denseMatrix
				.mapToPair(triplet -> new Tuple2<Integer, Tuple2<Integer, Double>>(
						triplet._2(), new Tuple2<Integer, Double>(triplet._1(),
								triplet._3())));
		JavaPairRDD<Integer, ArrayList<Tuple2<Integer, Double>>> denseMatrixAggregatedByCol = denseMatrixRowColInverted
				.aggregateByKey(new ArrayList<Tuple2<Integer, Double>>(), (
						list, tuple) -> {
					list.add(tuple);
					return list;
				}, (list1, list2) -> {
					list1.addAll(list2);
					return list1;
				});
		JavaRDD<IndexedRow> transposedMatrix = denseMatrixAggregatedByCol
				.map(data -> mapDataToRow(data));
		return new IndexedRowMatrix(transposedMatrix.rdd(), M.numCols(),
				toIntExact(M.numRows()));
	}
	
	/**
	 * Method create a new vector with its values inverted (in the multiplication sense).
	 * @param v The original vector.
	 * @return A vector containing all the values inverted.
	 */
	public static Vector invertVector(Vector v) {
		double[] values = v.copy().toArray();
		for (int i = 0; i < values.length; i++) {
			values[i] = 1.0d / values[i];
		}
		return Vectors.dense(values);
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
	 * Method the multiplies a column vector (v1) by a row vector (v2). v1 would
	 * be of size n x 1 for an arbitrary n and v2 would be of size 1 x m for an
	 * arbitrary m.
	 * 
	 * @param rowVector
	 *            The row vector.
	 * @param colVector
	 *            The column vector.
	 * @return The matrix of v1 * v2. The matrix is of size n x m.
	 */
	public static DenseMatrix multiplyColVectorByRowVector(Vector colVector,
			Vector rowVector) {
		int rowNumber = colVector.size();
		int colNumber = rowVector.size();
		double[] values = new double[rowNumber * colNumber];
		int index = 0;
		for (int col = 0; col < colNumber; col++) {
			for (int row = 0; row < rowNumber; row++) {
				values[index] = colVector.apply(row) * rowVector.apply(col);
				index++;
			}
		}
		return (DenseMatrix) Matrices.dense(rowNumber, colNumber, values);
	}

	/**
	 * Method that multiplies a matrix M to a diagonal matrix D that is on the
	 * right of M.
	 * 
	 * @param mat
	 *            The arbitrary matrix of size m x n.
	 * @param diagMat
	 *            The diagonal matrix represented by a vector of size n.
	 * @return A dense matrix M x D of size m x n.
	 */
	public static DenseMatrix multiplyMatrixByRightDiagonalMatrix(Matrix mat,
			Vector diagMat) {
		int rowNumber = mat.numRows();
		int colNumber = diagMat.size();
		double[] values = new double[rowNumber * colNumber];
		int index = 0;
		for (int col = 0; col < colNumber; col++) {
			for (int row = 0; row < rowNumber; row++) {
				values[index] = mat.apply(row, col) * diagMat.apply(col);
				index++;
			}
		}
		return (DenseMatrix) Matrices.dense(rowNumber, colNumber, values);
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
	 * Method that do the left multiplication of a diagonal matrix to an
	 * arbitrary matrix. Assume the diagonal matrix D is a m x n matrix and A is
	 * an arbitrary matrix of size m' x n'. Then we must have n = m'.
	 * 
	 * @param diagMatrix
	 *            The diagonal matrix in vector form.
	 * @param mat
	 *            The arbitrary matrix.
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
	
	public static CoordinateMatrix vectorToCoordinateMatrix(Vector vec, JavaSparkContext sc){
		double[] values = vec.toArray();
		List<MatrixEntry> entries = IntStream.range(0, values.length).mapToObj(index -> new MatrixEntry(index, index, values[index])).collect(Collectors.toList());
		return new CoordinateMatrix(SparkUtilities.elementsToJavaRDD(entries, sc).rdd(), values.length, values.length);
	}
	
	public static DenseMatrix vectorToDenseMatrix(Vector vec){
		return (DenseMatrix)Matrices.diag(vec);
	}

	/**
	 * Method that do the right multiplication of a diagonal matrix to an
	 * arbitrary matrix. Assume the diagonal matrix D is a m x n matrix and A is
	 * an arbitrary matrix of size m' x n'. Then we must have n' = m.
	 * 
	 * @param mat
	 *            The arbitrary matrix.
	 * @param diagMatrix
	 *            The diagonal matrix in vector form.
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
	 * Method that performs the scalar product between two double arrays. They
	 * must be the same size.
	 * 
	 * @param v1
	 *            The first array of double.
	 * @param v2
	 *            The second array of double
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
	 * Method that transforms an IndexedRowMatrix into a sparse local Matrix.
	 * 
	 * @param mat
	 *            The matrix in distributed form.
	 * @return The matrix in sparse local form.
	 */
	public static SparseMatrix toSparseLocalMatrix(IndexedRowMatrix mat) {
		ArrayList<Tuple3<Integer, Integer, Double>> triplets = new ArrayList<Tuple3<Integer, Integer, Double>>();
		triplets.addAll(mat.rows().toJavaRDD()
				.flatMap(row -> mapRowToSparseTriplets(row)).collect());
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
		return (SparseMatrix) Matrices.sparse(numRow, numCol, colPtrs, rowIndices, values);
	}

	/**
	 * Method that transforms an IndexedRowMatrix into a dense local Matrix.
	 * 
	 * @param mat
	 *            The matrix in distributed form.
	 * @return The matrix in dense local form.
	 */
	public static DenseMatrix toDenseLocalMatrix(IndexedRowMatrix mat) {
		int numRow = toIntExact(mat.numRows());
		int numCol = toIntExact(mat.numCols());
		double[] denseData = new double[numRow * numCol];
		List<IndexedRow> cols = transpose(mat).rows().toJavaRDD().collect();
		for (IndexedRow col : cols) {
			int colIndex = toIntExact(col.index());
			int destPos = colIndex * numRow;
			double[] colValues = col.vector().toArray();
			System.arraycopy(colValues, 0, denseData, destPos, colValues.length);
		}
		return (DenseMatrix)Matrices.dense(numRow, numCol, denseData);
	}

	/**
	 * Method that transforms a distributed matrix into a local list of vectors
	 * representing the original matrix.
	 * 
	 * @param mat
	 *            The distributed matrix to transform.
	 * @return A list of vector containing the rows of the given matrix. The
	 *         first row of the matrix correspond to the first element of the
	 *         list.
	 */
	public static List<Vector> toDenseLocalVectors(IndexedRowMatrix mat) {
		return getSecondArgumentAsList(mat
				.rows()
				.toJavaRDD()
				.mapToPair(
						row -> new Tuple2<Integer, Vector>(toIntExact(row
								.index()), indexedRowToDenseVector(row)))
				.sortByKey().collect());
	}

	/**
	 * Method that multiplies a row vector by a matrix. Assuming the vector v is
	 * of size m x 1 and the matrix A m' x n', we must have m = n'.
	 * 
	 * @param mat
	 *            The matrix to multiply.
	 * @param vec
	 *            The vector to multiply.
	 * @return The compressed Vector given by A * v of size m' x 1.
	 */
	public static Vector multiplyColumnVectorByMatrix(IndexedRowMatrix mat,
			Vector vec) {
		List<Double> results = mat.rows().toJavaRDD()
				.mapToDouble(row -> scalarProduct(vec, row.vector())).collect();
		return Vectors.dense(
				ArrayUtils.toPrimitive(results.toArray(new Double[results
						.size()]))).compressed();
	}

	/**
	 * Method that multiplies a row vector by a matrix. Assuming the vector v is
	 * of size 1 x n and the matrix A m' x n', we must have m = m'
	 * 
	 * @param vec
	 *            The vector to multiply.
	 * @param mat
	 *            The matrix to multiply.
	 * @return The compressed Vector given by v * A of size 1 x n'.
	 */
	public static Vector multiplyRowVectorByMatrix(Vector vec,
			IndexedRowMatrix mat) {
		IndexedRowMatrix transposedMat = transpose(mat);
		return multiplyColumnVectorByMatrix(transposedMat, vec);
	}

	/**
	 * Method that creates a dense vector from an indexed row.
	 * 
	 * @param row
	 *            The indexed row.
	 * @return A vector in dense representation.
	 */
	public static Vector indexedRowToDenseVector(IndexedRow row) {
		return Vectors.dense(row.vector().toArray());
	}

	/**
	 * Method that adds the missing row when converting a Coordinate matrix into
	 * an indexed row matrix. This is because the indexed row are in sparse
	 * representation, and if no matrix entries of the coordinate matrix
	 * corresponds to row 'i', then there won't be an indexed row of index i.
	 * 
	 * @param M
	 *            The coordinated matrix to be converted.
	 * @param sc
	 *            The java spark context that was used to create the coordinate
	 *            matrix, it is necessary to create the new rows.
	 * @return The indexed row matrix with an indexed row for each row index.
	 */
	public static IndexedRowMatrix getFullySpecifiedSparseIndexRowMatrixFromCoordinateMatrix(
			CoordinateMatrix M, JavaSparkContext sc) {
		final int numCol = toIntExact(M.numCols());
		JavaRDD<IndexedRow> indexedRowMatrix = M
				.entries()
				.toJavaRDD()
				.mapToPair(
						matEntry -> new Tuple2<Integer, Tuple2<Integer, Double>>(
								toIntExact(matEntry.i()),
								new Tuple2<Integer, Double>(toIntExact(matEntry
										.j()), matEntry.value())))
				.groupByKey()
				.map(rowAndSeqValues -> new IndexedRow(rowAndSeqValues._1(),
						Vectors.sparse(numCol, rowAndSeqValues._2())));
		// Adding the possible rows that had only zeros.
		List<Integer> rowIndexes = indexedRowMatrix.map(
				row -> toIntExact(row.index())).collect();
		List<IndexedRow> rowsToAdd = new ArrayList<IndexedRow>();
		for (int i = 0; i < toIntExact(M.numRows()); i++) {
			if (!rowIndexes.contains(i)) {
				rowsToAdd.add(new IndexedRow(i, Vectors.sparse(numCol,
						new int[] {}, new double[] {})));
			}
		}
		JavaRDD<IndexedRow> addedRows = SparkUtilities.elementsToJavaRDD(
				rowsToAdd, sc);
		return new IndexedRowMatrix(indexedRowMatrix.union(addedRows).rdd());
	}

	/**
	 * Method that return the correct indices for a symmetric matrix represented
	 * as an upper triangular matrices. For example the indices (2,0) would
	 * become (0,2).
	 * 
	 * @param rowIndexes
	 *            The row indexes of the entries.
	 * @param colIndexes
	 *            The col indexes of the entries.
	 */
	public static void convertToUpperTriangularMatrixIndices(int[] rowIndexes,
			int[] colIndexes) {
		for (int i = 0; i < rowIndexes.length; i++) {
			int oldRowIndex = rowIndexes[i];
			if (colIndexes[i] < rowIndexes[i]) {
				rowIndexes[i] = colIndexes[i];
				colIndexes[i] = oldRowIndex;
			}
		}
	}

	/**
	 * Method that verifies if a given entry is in another set of entry.
	 * 
	 * @param entryRowIndex
	 *            The row of the entry being verified
	 * @param entryColIndex
	 *            The col of the entry being verified
	 * @param entriesRow
	 *            The list of row indices of entries.
	 * @param entriesCol
	 *            The list of col indices of entries.
	 * @return True if the entry beigin verified is in the list of other
	 *         entries.
	 */
	public static boolean entryContainedInListOfEntries(int entryRowIndex,
			int entryColIndex, int[] entryRowIndexes, int[] entryColIndexes) {
		boolean contain = false;
		for (int i = 0; i < entryRowIndexes.length; i++) {
			if (entryRowIndex == entryRowIndexes[i]
					&& entryColIndex == entryColIndexes[i]) {
				contain = true;
				break;
			}
		}
		return contain;
	}
	
	/**
	 * Method that converts the data of a sparse matrix (i.e. the non-zero entries) into the Compressed Sparse Column (CSC) Format.
	 * @param numCols The number of column of the matrix.
	 * @param entries The non-zero entries of the matrix.
	 * @return The information needed to create a matrix in CSC representation.
	 */
	public static Tuple3<int[], int[], double[]> sparseMatrixFormatToCSCMatrixFormat(int numCols, List<MatrixEntry> entries){
		//Transform the entries into a map containing the tv show id as key and the list of user ids that watched the tv show (and their associated value) as values. 
		Map<Integer, List<Tuple2<Integer, Double>>> userIdsByTvShows = entries.stream().collect(Collectors.groupingBy(entry -> (int)entry.j(), Collectors.mapping(entry -> new Tuple2<Integer, Double>((int)entry.i(), entry.value()), Collectors.toList())));
		int[] colPtrs = initColPtrs(numCols);
		int[] rowIndices = new int[entries.size()];
		double[] values = new double[entries.size()];
		int currentColPtrsValue = 0;
		int currentIndex = 0;
		for(int tvShowIndex = 0; tvShowIndex < numCols; tvShowIndex++){
			List<Tuple2<Integer, Double>> userIdsValues = userIdsByTvShows.get(tvShowIndex);
			userIdsValues.sort((tuple1, tuple2) -> tuple1._1() - tuple2._1());
			colPtrs[tvShowIndex + 1] = currentColPtrsValue + userIdsValues.size();
			currentColPtrsValue += userIdsValues.size();
			for(Tuple2<Integer, Double> userIdValue : userIdsValues){
				rowIndices[currentIndex] = userIdValue._1();
				values[currentIndex] = userIdValue._2();
				currentIndex++;
			}
		}
		return new Tuple3<int[], int[], double[]>(colPtrs, rowIndices, values);
	}
	
	private static int[] initColPtrs(int numCols){
		int[] colPtrs = new int[numCols+1];
		colPtrs[0] = 0;
		return colPtrs;
	}

	public static void printIndexedRowMatrix(IndexedRowMatrix mat) {
		mat.rows()
				.toJavaRDD()
				.foreach(
						row -> System.out.println("( " + row.index() + ", "
								+ Arrays.toString(row.vector().toArray())
								+ " )"));
	}
	
	public static void printCoordinateMatrix(CoordinateMatrix mat){
		printIndexedRowMatrix(mat.toIndexedRowMatrix());
	}
	
	public static void printMatrix(Matrix mat){
		for(int row = 0; row < mat.numRows(); row++){
			for(int col = 0; col < mat.numCols(); col++){
				System.out.print(mat.apply(row, col) + " ");
			}
			System.out.println();
		}
	}

	private static void sortTripletsByColumn(
			List<Tuple3<Integer, Integer, Double>> triplets) {
		Collections.sort(triplets,
				new Comparator<Tuple3<Integer, Integer, Double>>() {

					@Override
					public int compare(
							Tuple3<Integer, Integer, Double> triplet1,
							Tuple3<Integer, Integer, Double> triplet2) {
						return triplet1._2().compareTo(triplet2._2());
					}
				});
	}

	private static int[] getColPtrsFromColIndices(int[] colIndices, int numCol) {
		int[] numberOfNonZerosPerColumn = new int[numCol];
		for (int colIndice : colIndices) {
			numberOfNonZerosPerColumn[colIndice]++;
		}
		int[] colPtrs = new int[numCol + 1];
		colPtrs[0] = 0;
		for (int i = 1; i < numCol + 1; i++) {
			colPtrs[i] = colPtrs[i - 1] + numberOfNonZerosPerColumn[i - 1];
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
		int rowIndex = toIntExact(row.index());
		for (int col = 0; col < colValues.length; col++) {
			values.add(new Tuple3<Integer, Integer, Double>(rowIndex, col,
					colValues[col]));
		}
		return values.iterator();
	}
}
