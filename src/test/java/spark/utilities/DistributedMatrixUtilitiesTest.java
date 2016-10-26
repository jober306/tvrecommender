package spark.utilities;

import static org.junit.Assert.assertArrayEquals;

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
	
	@AfterClass
	public static void tearDownOnce(){
		sc.close();
	}
}
