package model.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import model.UserTVProgramFixture;
import model.data.TVProgram;
import model.data.User;
import model.data.mapping.IdentityMapping;
import model.matrix.LocalUserTVProgramMatrix;
import model.measure.Measure;
import model.measure.similarity.NormalizedCosineSimilarity;

import org.apache.spark.mllib.linalg.Matrix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalUserTVProgramMatrixTest extends UserTVProgramFixture{
	
	static IdentityMapping<User> userMapping;
	static IdentityMapping<TVProgram> tvProgramMapping;
	LocalUserTVProgramMatrix<User, User, TVProgram, TVProgram> denseMatrix;
	LocalUserTVProgramMatrix<User, User, TVProgram, TVProgram> sparseMatrix;
	
	@Before
	public void setUp(){
		userMapping = new IdentityMapping<>(allUsers);
		tvProgramMapping = new IdentityMapping<>(allPrograms);
		this.denseMatrix = new LocalUserTVProgramMatrix<>(NUM_ROWS, NUM_COL, MATRIX_VALUES, userMapping, tvProgramMapping);
		this.sparseMatrix = new LocalUserTVProgramMatrix<>(NUM_ROWS, NUM_COL, COL_PTRS, ROW_INDICES, SPARSE_MATRIX_VALUES, userMapping, tvProgramMapping);
	}
	
	@Test
	public void constructorDenseTest(){
		double[] actualValues = new double[NUM_ROWS * NUM_COL];
		for(int col = 0; col < NUM_COL; col++){
			for(int row = 0; row < NUM_ROWS; row++){
				actualValues[col * NUM_ROWS + row] = denseMatrix.getValue(row, col);
			}
		}
		assertArrayEquals(MATRIX_VALUES, actualValues, 0.0d);
	}
	
	@Test
	public void constructorSparseTest(){
		double[] actualValues = new double[NUM_ROWS * NUM_COL];
		for(int col = 0; col < NUM_COL; col++){
			for(int row = 0; row < NUM_ROWS; row++){
				actualValues[col * NUM_ROWS + row] = sparseMatrix.getValue(row, col);
			}
		}
		assertArrayEquals(MATRIX_VALUES, actualValues, 0.0d);
	}
	
	@Test
	public void getterTest(){
		assertEquals(NUM_ROWS, (int)denseMatrix.getNumRows());
		assertEquals(NUM_ROWS, (int)sparseMatrix.getNumRows());
		assertEquals(NUM_COL, (int)denseMatrix.getNumCols());
		assertEquals(NUM_COL, (int)denseMatrix.getNumCols());
	}
	
	@Test
	public void getRowTest(){
		double[] expectedValues = {4,2,0,0};
		double[] actualDenseValues = denseMatrix.getRow(2).toArray();
		double[] actualSparseValues = sparseMatrix.getRow(2).toArray();
		assertArrayEquals(expectedValues, actualDenseValues, 0.0d);
		assertArrayEquals(expectedValues, actualSparseValues, 0.0d);
	}
	
	@Test
	public void getColumnTest(){
		double[] expectedValues = {1,3,0};
		double[] actualDenseValues = denseMatrix.getColumn(3).toArray();
		double[] actualSparseValues = denseMatrix.getColumn(3).toArray();
		assertArrayEquals(expectedValues, actualDenseValues, 0.0d);
		assertArrayEquals(expectedValues, actualSparseValues, 0.0d);
	}
	
	@Test
	public void getItemSimilaritiesTest(){
		Measure measure = NormalizedCosineSimilarity.getInstance();
		Matrix denseS = denseMatrix.getItemSimilarities(measure);
		Matrix sparseS = sparseMatrix.getItemSimilarities(measure);
		int expectedMatrixSize = 4;
		assertEquals(expectedMatrixSize, sparseS.numRows());
		assertEquals(expectedMatrixSize, sparseS.numCols());
		assertEquals(denseS.numRows(), sparseS.numRows());
		assertEquals(denseS.numCols(), sparseS.numCols());
		for(int col1 = 0; col1 < expectedMatrixSize; col1++){
			for(int col2 = 0; col2 < expectedMatrixSize; col2++){
				double expectedDenseSim = measure.calculate(denseMatrix.getColumn(col1), denseMatrix.getColumn(col2));
				double expectedSparseSim = measure.calculate(sparseMatrix.getColumn(col1), sparseMatrix.getColumn(col2));
				if(col1 == col2){
					expectedDenseSim = 1.0d;
					expectedSparseSim = 1.0d;
				}
				double actualDenseSim = denseS.apply(col1, col2);
				double actualSparseSim = sparseS.apply(col1, col2);
				assertEquals(expectedDenseSim, actualDenseSim, 0.001d);
				assertEquals(expectedSparseSim, actualSparseSim, 0.001d);
			}
		}
	}
	
	@After
	public void tearDown(){
		this.denseMatrix = null;
		this.sparseMatrix = null;
	}
}	
