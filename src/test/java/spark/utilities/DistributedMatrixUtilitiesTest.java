package spark.utilities;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static java.lang.Math.toIntExact;


import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DistributedMatrixUtilitiesTest {
	
	private static final double[][] matrixValues = {{1.0d,2.0d,5.0d,2.0d},{3.0d,2.0d,4.0d,1.0d}};
	
	static IndexedRowMatrix R;
	static JavaRDD<IndexedRow> rows;
	static JavaSparkContext sc;
	
	@BeforeClass
	public static void setUpOnce(){
		sc = SparkUtilities.getADefaultSparkContext();
		List<IndexedRow> rowList = new ArrayList<IndexedRow>();
		for(int i = 0; i < matrixValues.length; i++){
			rowList.add(new IndexedRow(i, Vectors.dense(matrixValues[i])));
		}
		rows = SparkUtilities.<IndexedRow>elementsToJavaRDD(rowList, sc);
		R = new IndexedRowMatrix(rows.rdd());
	}
	
	@Test
	public void transposeTest(){
		IndexedRowMatrix Rt = DistributedMatrixUtilities.transpose(R);
		double[][] expectedValues = {{1.0d,3.0d},{2.0d,2.0d},{5.0d,4.0d},{2.0d,1.0d}};
		for(int rowIndex = 0; rowIndex < 4; rowIndex++){
			final int rowIndexFinal = rowIndex;
			assertArrayEquals(expectedValues[rowIndex], Rt.rows().toJavaRDD().filter(row -> row.index() == rowIndexFinal).collect().get(0).vector().toArray(),0.0d);
		}
	}
	
	@Test
	public void inverseDiagonalMatrixTest(){
		IndexedRowMatrix inv = DistributedMatrixUtilities.inverseDiagonalMatrix(R);
		double[] expectedValues = {1.0d, 0.5d};
		inv.rows().toJavaRDD().foreach(row ->{
			if(row.index() == 0){
				assertEquals(expectedValues[0], row.vector().apply(0),0.0d);
			}
			else if(row.index() == 1){
				assertEquals(expectedValues[1], row.vector().apply(1), 0.0d);
			}
			for(int i = 0; i < matrixValues[0].length; i++){
				if(i != row.index()){
					assertEquals(matrixValues[toIntExact(row.index())][i], row.vector().apply(i),0.0d);
				}
			}
		});
	}
	
	@Test
	public void hardThresholdTest(){
		IndexedRowMatrix hardThresholdedMat = DistributedMatrixUtilities.hardThreshold(R, 1);
		hardThresholdedMat.rows().toJavaRDD().foreach(row ->{
			if(row.index() == 0){
				assertEquals(0.0d, row.vector().apply(0),0.0d);
			}
			for(int i = 0; i < matrixValues[0].length; i++){
				if(row.index() == 0)
					i++;
				assertEquals(matrixValues[toIntExact(row.index())][i], row.vector().apply(i),0.0d);
			}
		});
	}
	
	@AfterClass
	public static void tearDownOnce(){
		sc.close();
	}
}
