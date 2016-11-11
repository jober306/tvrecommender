package mllib.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.lamp.fjbg.JConstantPool.Entry;
import scala.Tuple2;
import spark.utilities.SparkUtilities;

public class DistributedUserItemMatrixTest {
	
	private static final int[][] matrixIndices = {{0,3,4,7,8},{},{1,2,3}};
	private static final double[][] matrixValues = {{1.0d,2.0d,5.0d,2.0d,1.0d},{},{3.0d,2.0d,4.0d}};
	private static final int matrixNumberOfColumn = 10;
	
	static DistributedUserItemMatrix R;
	static JavaRDD<IndexedRow> rows;
	static JavaSparkContext sc;
	
	@BeforeClass
	public static void setUp(){
		sc = SparkUtilities.getADefaultSparkContext();
		List<IndexedRow> rowList = new ArrayList<IndexedRow>();
		for(int i = 0; i < matrixIndices.length; i++){
			rowList.add(new IndexedRow(i, Vectors.sparse(matrixNumberOfColumn, matrixIndices[i], matrixValues[i])));
		}
		rows = SparkUtilities.<IndexedRow>elementsToJavaRDD(rowList, sc);
		R = new DistributedUserItemMatrix(rows);
	}
	
	@Test
	public void getRowTest() {
		for(int row = 0; row < matrixIndices.length; row++){
			IndexedRow indexedRow = R.getRow(row);
			SparseVector sparseVec = indexedRow.vector().toSparse();
			assertArrayEquals(sparseVec.indices(), matrixIndices[row]);
			assertArrayEquals(sparseVec.values(), matrixValues[row], 0.0d);
		}
	}
	
	@Test
	public void getValueTest(){
		for(int row = 0; row < matrixIndices.length; row++){
			ArrayList<Integer> indices = new ArrayList<Integer>(Arrays.asList(ArrayUtils.toObject(matrixIndices[row])));
			for(int col = 0; col < matrixNumberOfColumn; col++){
				int valueIndex = indices.indexOf(col);
				if(valueIndex == -1){
					assertEquals(0.0d, R.getValue(row, col),0.0d);
				}
				else{
					assertEquals(matrixValues[row][valueIndex], R.getValue(row, col), 0.0d);
				}
			}
		}
	}
	
	@Test
	public void getItemSimilaritiesTest(){
		CoordinateMatrix S = R.getItemSimilarities();
		List<MatrixEntry> entries = S.entries().toJavaRDD().collect();
		Set<Tuple2<Long,Long>> entriesSet = new HashSet<Tuple2<Long,Long>>();
		for(MatrixEntry entry : entries){
			entriesSet.add(new Tuple2<Long, Long>(entry.i(), entry.j()));
		}
		assertEquals(entriesSet.size(), entries.size());
	}
	
	@AfterClass
	public static void tearDownOnce(){
		sc.close();
	}

}
