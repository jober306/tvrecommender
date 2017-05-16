package util;

import static java.lang.Math.toIntExact;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MllibUtilitiesTest {

	private static final double[][] matrixValues = {
			{ 1.0d, 2.0d, 5.0d, 2.0d }, { 3.0d, 2.0d, 4.0d, 1.0d } };

	static IndexedRowMatrix R;
	static JavaRDD<IndexedRow> rows;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		List<IndexedRow> rowList = new ArrayList<IndexedRow>();
		for (int i = 0; i < matrixValues.length; i++) {
			rowList.add(new IndexedRow(i, Vectors.dense(matrixValues[i])));
		}
		rows = SparkUtilities.<IndexedRow> elementsToJavaRDD(rowList, sc);
		R = new IndexedRowMatrix(rows.rdd());
	}

	@Test
	public void transposeTest() {
		IndexedRowMatrix Rt = MllibUtilities.transpose(R);
		double[][] expectedValues = { { 1.0d, 3.0d }, { 2.0d, 2.0d },
				{ 5.0d, 4.0d }, { 2.0d, 1.0d } };
		for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
			final int rowIndexFinal = rowIndex;
			assertArrayEquals(
					expectedValues[rowIndex],
					Rt.rows().toJavaRDD()
							.filter(row -> row.index() == rowIndexFinal)
							.collect().get(0).vector().toArray(), 0.0d);
		}
	}

	@Test
	public void inverseDiagonalMatrixTest() {
		IndexedRowMatrix inv = MllibUtilities.inverseDiagonalMatrix(R);
		double[] expectedValues = { 1.0d, 0.5d };
		inv.rows()
				.toJavaRDD()
				.foreach(
						row -> {
							if (row.index() == 0) {
								assertEquals(expectedValues[0], row.vector()
										.apply(0), 0.0d);
							} else if (row.index() == 1) {
								assertEquals(expectedValues[1], row.vector()
										.apply(1), 0.0d);
							}
							for (int i = 0; i < matrixValues[0].length; i++) {
								if (i != row.index()) {
									assertEquals(
											matrixValues[toIntExact(row.index())][i],
											row.vector().apply(i), 0.0d);
								}
							}
						});
	}

	@Test
	public void hardThresholdMatrixTest() {
		int hardThresholdValue = 1;
		IndexedRowMatrix hardThresholdedMat = MllibUtilities.hardThreshold(R,
				hardThresholdValue);
		hardThresholdedMat
				.rows()
				.toJavaRDD()
				.foreach(
						row -> {
							if (row.index() == 0) {
								assertEquals(0.0d, row.vector().apply(0), 0.0d);
							}
							for (int i = 0; i < matrixValues[0].length; i++) {
								if (row.index() == 0)
									i++;
								assertEquals(
										matrixValues[toIntExact(row.index())][i],
										row.vector().apply(i), 0.0d);
							}
						});
	}

	@Test
	public void hardThresholdVectorTest() {
		int hardThresholdValue = 2;
		Vector hardThresholdedMat = MllibUtilities.hardThreshold(
				Vectors.dense(new double[] { 1.0d, 2.0d, 5.0d, 2.0d }), 2);
		double[] hardThresholdedValues = hardThresholdedMat.toArray();
		for (int i = 0; i < hardThresholdedValues.length; i++) {
			if (i >= hardThresholdValue) {
				assertEquals(0.0d, hardThresholdedValues[i], 0.0d);
			} else {
				assertEquals(matrixValues[0][i], hardThresholdedValues[i], 0.0d);
			}
		}
	}

	@Test
	public void multiplyColVectorByRowVectorTest() {
		Vector rowVec = Vectors.dense(new double[] { 2.0d, 0.0d, 3.0d });
		Vector colVec = Vectors.dense(new double[] { 1.0d, 2.0d, 3.0d, 4.0d });
		DenseMatrix result = MllibUtilities.multiplyColVectorByRowVector(
				colVec, rowVec);
		int expectedColSize = rowVec.size();
		int expectedRowSize = colVec.size();
		assertEquals(expectedRowSize, result.numRows());
		assertEquals(expectedColSize, result.numCols());
		double[] expectedValues = new double[] { 2, 4, 6, 8, 0, 0, 0, 0, 3, 6,
				9, 12 };
		double[] actualValues = result.toArray();
		assertArrayEquals(expectedValues, actualValues, 0.0d);
	}

	@Test
	public void multiplyMatrixByRightDiagonalMatrixTest() {
		Matrix mat = Matrices.dense(3, 2, new double[] { 1, 4, 2, 1, 0, 1 });
		Vector diagMat = Vectors.dense(new double[] { 3, 1 });
		DenseMatrix result = MllibUtilities
				.multiplyMatrixByRightDiagonalMatrix(mat, diagMat);
		int expectedRowSize = mat.numRows();
		int expectedColSize = diagMat.size();
		assertEquals(expectedRowSize, result.numRows());
		assertEquals(expectedColSize, result.numCols());
		double[] expectedValues = new double[] { 3, 12, 6, 1, 0, 1 };
		double[] actualValues = result.toArray();
		assertArrayEquals(expectedValues, actualValues, 0.0d);
	}

	@Test
	public void multiplicateByLeftDiagonalMatrixGoodSizeTest() {
		Vector diagMat = Vectors.dense(new double[] { 1.0d, 2.0d });
		double[][] expectedValues = { { 1, 2, 5, 2 }, { 6, 4, 8, 2 } };
		IndexedRowMatrix result = MllibUtilities
				.multiplicateByLeftDiagonalMatrix(diagMat, R);
		result.rows()
				.toJavaRDD()
				.foreach(
						row -> {
							int rowIndex = toIntExact(row.index());
							assertArrayEquals(expectedValues[rowIndex], row
									.vector().toArray(), 0.0d);
						});
	}

	@Test
	public void multiplicateByRightDiagonalMatrixGoodSizeTest() {
		Vector diagMat = Vectors.dense(new double[] { 1.0d, 2.0d, 3.0d, 4.0d });
		double[][] expectedValues = { { 1, 4, 15, 8 }, { 3, 4, 12, 4 } };
		IndexedRowMatrix result = MllibUtilities
				.multiplicateByRightDiagonalMatrix(R, diagMat);
		result.rows()
				.toJavaRDD()
				.foreach(
						row -> {
							int rowIndex = toIntExact(row.index());
							assertArrayEquals(expectedValues[rowIndex], row
									.vector().toArray(), 0.0d);
						});
	}

	@Test
	public void scalarProductTest() {
		Vector v1 = Vectors.dense(new double[] { 1, 2, 3, 4 });
		Vector v2 = Vectors.dense(new double[] { 4, 3, 2, 1 });
		double expectedValue = 20.0d;
		double actualValue = MllibUtilities.scalarProduct(v1, v2);
		assertEquals(expectedValue, actualValue, 0.0d);
	}

	@Test
	public void toSparseLocalMatrixTest() {
		Matrix actualMatrix = MllibUtilities.toSparseLocalMatrix(R);
		for (int rowIndex = 0; rowIndex < matrixValues.length; rowIndex++) {
			for (int colIndex = 0; colIndex < matrixValues[0].length; colIndex++) {
				assertEquals(matrixValues[rowIndex][colIndex],
						actualMatrix.apply(rowIndex, colIndex), 0.0d);
			}
		}
	}

	@Test
	public void toDenseLocalMatrixTest() {
		Matrix actualMatrix = MllibUtilities.toDenseLocalMatrix(R);
		for (int rowIndex = 0; rowIndex < matrixValues.length; rowIndex++) {
			for (int colIndex = 0; colIndex < matrixValues[0].length; colIndex++) {
				assertEquals(matrixValues[rowIndex][colIndex],
						actualMatrix.apply(rowIndex, colIndex), 0.0d);
			}
		}
	}

	@Test
	public void toDenseLocalVectorsTest() {
		List<Vector> vectors = MllibUtilities.toDenseLocalVectors(R);
		for (int i = 0; i < matrixValues.length; i++) {
			System.out.println(vectors.get(i));
			assertArrayEquals(matrixValues[i], vectors.get(i).toArray(), 0.0d);
		}
	}

	@Test
	public void toSparseLocalMatrixWithEmptyColTest() {
		double[][] emptyColValues = { { 0, 3, 0, 0 }, { 0, 0, 4, 5 },
				{ 0, 4, 0, 0 } };
		List<IndexedRow> rowList = new ArrayList<IndexedRow>();
		for (int i = 0; i < emptyColValues.length; i++) {
			rowList.add(new IndexedRow(i, Vectors.dense(emptyColValues[i])));
		}
		IndexedRowMatrix emptyColMatrix = new IndexedRowMatrix(SparkUtilities
				.<IndexedRow> elementsToJavaRDD(rowList, sc).rdd());
		Matrix actualMatrix = MllibUtilities
				.toSparseLocalMatrix(emptyColMatrix);
		for (int rowIndex = 0; rowIndex < emptyColValues.length; rowIndex++) {
			for (int colIndex = 0; colIndex < emptyColValues[0].length; colIndex++) {
				assertEquals(emptyColValues[rowIndex][colIndex],
						actualMatrix.apply(rowIndex, colIndex), 0.0d);
			}
		}
	}

	@Test
	public void multiplyColumnVectorByMatrixTest() {
		Vector vec = Vectors.dense(new double[] { 1, 0, 2, 3 });
		double[] expectedValues = { 17, 14 };
		double[] actualValues = MllibUtilities.multiplyColumnVectorByMatrix(R,
				vec).toArray();
		assertEquals(expectedValues.length, actualValues.length);
		assertArrayEquals(expectedValues, actualValues, 0.0d);
	}

	@Test
	public void multiplyRowVectorByMatrixTest() {
		Vector vec = Vectors.dense(new double[] { 2, 3 });
		double[] expectedValues = { 11, 10, 22, 7 };
		double[] actualValues = MllibUtilities
				.multiplyRowVectorByMatrix(vec, R).toArray();
		assertEquals(expectedValues.length, actualValues.length);
		assertArrayEquals(expectedValues, actualValues, 0.0d);
	}

	@Test
	public void getFullySpecifiedSparseIndexRowMatrixFromCoordinateMatrixTest() {
		List<MatrixEntry> matrixEntries = new ArrayList<MatrixEntry>();
		int numberOfRow = 3;
		int numberOfCol = 2;
		/**
		 * Creating the following coordinate matrix 0 1 1 0 0 0
		 */
		double[][] coordMatValues = { { 0, 1 }, { 1, 0 }, { 0, 0 } };
		matrixEntries.add(new MatrixEntry(0, 1, coordMatValues[0][1]));
		matrixEntries.add(new MatrixEntry(1, 0, coordMatValues[1][0]));
		CoordinateMatrix coordMatrix = new CoordinateMatrix(SparkUtilities
				.elementsToJavaRDD(matrixEntries, sc).rdd(), numberOfRow,
				numberOfCol);
		IndexedRowMatrix actualMatrix = MllibUtilities
				.getFullySpecifiedSparseIndexRowMatrixFromCoordinateMatrix(
						coordMatrix, sc);
		List<IndexedRow> rows = actualMatrix.rows().toJavaRDD().collect();
		assertEquals(numberOfRow, rows.size());
		for (IndexedRow row : rows) {
			int rowIndex = toIntExact(row.index());
			assertArrayEquals(coordMatValues[rowIndex], row.vector().toArray(),
					0.0d);
		}
	}

	@After
	public void verifyMatrixRInvariant() {
		List<IndexedRow> rows = R.rows().toJavaRDD().collect();
		assertEquals(matrixValues.length, rows.size());
		for (IndexedRow row : rows) {
			int rowIndex = toIntExact(row.index());
			assertArrayEquals(matrixValues[rowIndex], row.vector().toArray(),
					0.0d);
		}
	}

	@AfterClass
	public static void tearDownOnce() {
		sc.close();
	}
}
