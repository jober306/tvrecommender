package model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.similarity.NormalizedCosineSimilarity;
import util.spark.SparkUtilities;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.primitives.Ints;

public class DistributedUserItemMatrixTest {

	private static final int[][] matrixIndices = { { 0, 3, 4, 7, 8 }, {},
			{ 1, 2, 3 } };
	private static final double[][] matrixValues = {
			{ 1.0d, 2.0d, 5.0d, 2.0d, 1.0d }, {}, { 3.0d, 2.0d, 4.0d } };
	private static final double[][] smallMatrixValues = {{1,1}, {0,2}, {2,1}};
	private static final double expectedSim = (double)3 / (Math.sqrt(5) * Math.sqrt(6));
	private static final double[][] expectedSimilarities = {{1,expectedSim},{expectedSim,1}};
	private static final int matrixNumberOfColumn = 10;

	static DistributedUserItemMatrix R;
	static DistributedUserItemMatrix smallR;
	static JavaRDD<IndexedRow> rows;
	static JavaRDD<IndexedRow> smallRows;
	static JavaSparkContext sc;

	@BeforeClass
	public static void setUp() {
		sc = SparkUtilities.getADefaultSparkContext();
		List<IndexedRow> rowList = new ArrayList<IndexedRow>();
		List<IndexedRow> smallRowList = new ArrayList<IndexedRow>();
		for (int i = 0; i < matrixIndices.length; i++) {
			rowList.add(new IndexedRow(i, Vectors.sparse(matrixNumberOfColumn,
					matrixIndices[i], matrixValues[i])));
			smallRowList.add(new IndexedRow(i, Vectors.dense(smallMatrixValues[i])));
		}
		rows = SparkUtilities.elementsToJavaRDD(rowList, sc);
		smallRows = SparkUtilities.elementsToJavaRDD(smallRowList, sc);
		R = new DistributedUserItemMatrix(rows);
		smallR = new DistributedUserItemMatrix(smallRows);
		
	}

	@Test
	public void getRowTest() {
		for (int row = 0; row < matrixIndices.length; row++) {
			SparseVector sparseVec = R.getRow(row).toSparse();
			assertArrayEquals(sparseVec.indices(), matrixIndices[row]);
			assertArrayEquals(sparseVec.values(), matrixValues[row], 0.0d);
		}
	}

	@Test
	public void getValueTest() {
		for (int row = 0; row < matrixIndices.length; row++) {
			ArrayList<Integer> indices = new ArrayList<Integer>(
					Arrays.asList(ArrayUtils.toObject(matrixIndices[row])));
			for (int col = 0; col < matrixNumberOfColumn; col++) {
				int valueIndex = indices.indexOf(col);
				if (valueIndex == -1) {
					assertEquals(0.0d, R.getValue(row, col), 0.0d);
				} else {
					assertEquals(matrixValues[row][valueIndex],
							R.getValue(row, col), 0.0d);
				}
			}
		}
	}

	@Test
	public void getItemIndexesSeenByUserTest() {
		for (int userIndex = 0; userIndex < matrixIndices.length; userIndex++) {
			assertEquals(Ints.asList(matrixIndices[userIndex]),
					R.getItemIndexesSeenByUser(userIndex));
		}
	}

	@Test
	public void getItemSimilaritiesTest() {
		Matrix S = smallR.getItemSimilarities(NormalizedCosineSimilarity.getInstance());
		int expectedNumRows = 2;
		int expectedNumCols = 2;
		assertEquals(expectedNumRows, S.numRows());
		assertEquals(expectedNumCols, S.numCols());
		for(int row = 0; row < expectedNumRows; row++){
			for(int col = 0; col < expectedNumCols; col++){
				assertEquals(expectedSimilarities[row][col], S.apply(row, col),0.001);
			}
		}
	}
	
	@Test
	public void getItemSimilaritiesDefaultSimilarityTest() {
		Matrix S = smallR.getItemSimilarities(sc).toBlockMatrix().toLocalMatrix();
		System.out.println(S.toString());
		int expectedNumRows = 2;
		int expectedNumCols = 2;
		assertEquals(expectedNumRows, S.numRows());
		assertEquals(expectedNumCols, S.numCols());
		for(int row = 0; row < expectedNumRows; row++){
			for(int col = 0; col < expectedNumCols; col++){
				assertEquals(expectedSimilarities[row][col], S.apply(row, col),0.001);
			}
		}
	}

	@AfterClass
	public static void tearDownOnce() {
		sc.close();
	}

}
