package model.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import model.UserTVProgramFixture;
import model.data.TVProgram;
import model.data.User;
import model.matrix.DistributedUserTVProgramMatrix;
import model.similarity.NormalizedCosineSimilarity;
import model.similarity.SimilarityMeasure;
import util.spark.SparkUtilities;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DistributedUserTVProgramMatrixTest extends UserTVProgramFixture{

	static JavaSparkContext sc;
	
	static DistributedUserTVProgramMatrix<User, TVProgram> R;
	static UserTVProgramMapping<User, TVProgram> mapping;
	
	@BeforeClass
	public static void setUp() {
		sc = SparkUtilities.getADefaultSparkContext();
		List<IndexedRow> rowList = new ArrayList<IndexedRow>();
		for (int rowIndex = 0; rowIndex < NUM_ROWS; rowIndex++) {
			rowList.add(new IndexedRow(rowIndex, Vectors.sparse(NUM_COL, ROW_VALUE_INDICES[rowIndex], ROW_SPARSE_MATRIX_VALUES[rowIndex])));
		}
		JavaRDD<IndexedRow> rows = SparkUtilities.elementsToJavaRDD(rowList, sc);
		R = new DistributedUserTVProgramMatrix<>(rows, mapping);
		
	}

	@Test
	public void getValueTest(){
		double[] actualValues = new double[NUM_ROWS * NUM_COL];
		for(int col = 0; col < NUM_COL; col++){
			for(int row = 0; row < NUM_ROWS; row++){
				actualValues[col * NUM_ROWS + row] = R.getValue(row, col);
			}
		}
		assertArrayEquals(MATRIX_VALUES, actualValues, 0.0d);
	}
	
	@Test
	public void getterTest(){
		assertEquals(NUM_ROWS, (int)R.getNumRows());
		assertEquals(NUM_COL, (int)R.getNumCols());
	}
	
	@Test
	public void getRowTest(){
		double[] expectedValues = {4,2,0,0};
		double[] actualValues = R.getRow(2).toArray();
		assertArrayEquals(expectedValues, actualValues, 0.0d);
	}
	
	@Test
	public void getColumnTest(){
		double[] expectedValues = {1,3,0};
		double[] actualValues = R.getColumn(3).toArray();
		assertArrayEquals(expectedValues, actualValues, 0.0d);
	}
	
	@Test
	public void getItemSimilaritiesTest(){
		SimilarityMeasure measure = NormalizedCosineSimilarity.getInstance();
		Matrix S = R.getItemSimilarities(measure);
		int expectedMatrixSize = 4;
		assertEquals(expectedMatrixSize, S.numRows());
		assertEquals(expectedMatrixSize, S.numCols());
		for(int col1 = 0; col1 < expectedMatrixSize; col1++){
			for(int col2 = 0; col2 < expectedMatrixSize; col2++){
				double expectedSim = measure.calculateSimilarity(R.getColumn(col1), R.getColumn(col2));
				if(col1 == col2){
					expectedSim = 1.0d;
				}
				double actualSim = S.apply(col1, col2);
				assertEquals(expectedSim, actualSim, 0.001d);
			}
		}
	}

	@AfterClass
	public static void tearDownOnce() {
		sc.close();
	}

}
