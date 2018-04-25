package model;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import model.matrix.LocalUserItemMatrix;
import model.similarity.NormalizedCosineSimilarity;

import org.apache.spark.mllib.linalg.Matrix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalUserItemMatrixTest {
	
	
	/**
	 * A 3x4 matrix with the given values
	 */
	static final int NUM_ROWS = 3;
	static final int NUM_COL = 4;
	static final double[] MATRIX_VALUES = {0,1,4,3,0,2,0,0,0,0,0,0};
	static final double[] SPARSE_MATRIX_VALUES = {1,4,3,2};
	static final int[] COL_PTRS = {0,2,4,4,4};
	static final int[] ROW_INDICES = {1,2,0,2};
	
	LocalUserItemMatrix denseMatrix;
	LocalUserItemMatrix sparseMatrix;
	
	@Before
	public void setUp(){
		this.denseMatrix = new LocalUserItemMatrix(NUM_ROWS, NUM_COL, MATRIX_VALUES);
		this.sparseMatrix = new LocalUserItemMatrix(NUM_ROWS, NUM_COL, COL_PTRS, ROW_INDICES, SPARSE_MATRIX_VALUES);
	}
	
	@Test
	public void constructorDenseTest(){
		double[] actualValues = new double[NUM_ROWS * NUM_COL];
		for(int col = 0; col < NUM_COL; col++){
			for(int row = 0; row < NUM_ROWS; row++){
				actualValues[col * NUM_ROWS + row] = denseMatrix.getValue(row, col);
			}
		}
		assertThat(MATRIX_VALUES, is(equalTo(actualValues)));
	}
	
	@Test
	public void constructorSparseTest(){
		double[] actualValues = new double[NUM_ROWS * NUM_COL];
		for(int col = 0; col < NUM_COL; col++){
			for(int row = 0; row < NUM_ROWS; row++){
				actualValues[col * NUM_ROWS + row] = sparseMatrix.getValue(row, col);
			}
		}
		assertThat(MATRIX_VALUES, is(equalTo(actualValues)));
	}
	
	@Test
	public void getterTest(){
		assertThat(NUM_ROWS, both(equalTo((int)denseMatrix.getNumRows())).and(equalTo((int)sparseMatrix.getNumRows())));
		assertThat(NUM_COL, both(equalTo((int) denseMatrix.getNumCols())).and(equalTo((int)sparseMatrix.getNumCols())));
	}
	
	@Test
	public void getRowTest(){
		double[] expectedValues = {4,2,0,0};
		double[] actualDenseValues = denseMatrix.getRow(2).toArray();
		double[] actualSparseValues = sparseMatrix.getRow(2).toArray();
		assertThat(expectedValues, both(equalTo(actualDenseValues)).and(equalTo(actualSparseValues)));
	}
	
	@Test
	public void getIndexesSeenByUserTest(){
		List<Integer> expectedValues = Arrays.asList(0,1);
		List<Integer> actualDenseValues = denseMatrix.getItemIndexesSeenByUser(2);
		List<Integer> actualSparseValues = sparseMatrix.getItemIndexesSeenByUser(2);
		assertThat(expectedValues, both(equalTo(actualDenseValues)).and(equalTo(actualSparseValues)));
	}
	
	@Test
	public void getItemSimilaritiesTest(){
		LocalUserItemMatrix M = new LocalUserItemMatrix(3, 2, new double[]{1,2,0,0,1,2});
		double[][] expectedSimilarities = new double[][] {{1, 0.4}, {0.4, 1}};
		Matrix S = M.getItemSimilarities(NormalizedCosineSimilarity.getInstance());
		assertThat(2, equalTo(S.numRows()));
		assertThat(2, equalTo(S.numCols()));
		for(int row = 0; row < S.numRows(); row++){
			for(int col = 0; col < S.numCols(); col++){
				assertEquals(expectedSimilarities[row][col], S.apply(row, col), 0.01);
			}
		}
	}
	
	@After
	public void tearDown(){
		this.denseMatrix = null;
		this.sparseMatrix = null;
	}
}	
