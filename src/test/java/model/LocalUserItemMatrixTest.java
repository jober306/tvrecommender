package model;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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
		int[] expectedValues = {0,1};
		int[] actualDenseValues = denseMatrix.getItemIndexesSeenByUser(2);
		int[] actualSparseValues = sparseMatrix.getItemIndexesSeenByUser(2);
		assertThat(expectedValues, both(equalTo(actualDenseValues)).and(equalTo(actualSparseValues)));
	}
	
	@After
	public void tearDown(){
		this.denseMatrix = null;
		this.sparseMatrix = null;
	}
}	
