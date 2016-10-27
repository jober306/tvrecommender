package spark.utilities;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.graphx.TripletFields;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.sql.execution.vectorized.ColumnarBatch.Row;

import scala.Tuple2;
import scala.Tuple3;

/**
 * Class that offers multiple utility function on mlllib distributed matrix object.
 * @author Jonathan Bergeron
 *
 */
public class DistributedMatrixUtilities {
	
	/**
	 * Method that transposes the given matrix.
	 * @param M The matrix to be transposed.
	 * @return The transpose of the given matrix.
	 */
	public static IndexedRowMatrix transpose(IndexedRowMatrix M){
		JavaRDD<IndexedRow> transposedMatrix = M.rows().toJavaRDD().<Tuple3<Integer,Integer,Double>>flatMap(row -> mapRowToData(row)).mapToPair(triplet -> new Tuple2<Integer, Tuple2<Integer,Double>>(triplet._2(), new Tuple2<Integer,Double>(triplet._1(),triplet._3())))
		.aggregateByKey(new ArrayList<Tuple2<Integer,Double>>(), (list, tuple) -> {list.add(tuple); return list;}, (list1,list2)-> {list1.addAll(list2); return list1;})
		.map(data -> mapDataToRow(data));
		return new IndexedRowMatrix(transposedMatrix.rdd());
	}
	
	private static Iterator<Tuple3<Integer,Integer,Double>> mapRowToData(IndexedRow row){
		List<Tuple3<Integer,Integer,Double>> values = new ArrayList<Tuple3<Integer,Integer,Double>>();
		double[] colValues = row.vector().toArray();
		for(int col = 0; col < colValues.length; col++){
			values.add(new Tuple3<Integer,Integer,Double>(toIntExact(row.index()), col, colValues[col]));
		}
		return values.iterator();
	}
	
	private static IndexedRow mapDataToRow(Tuple2<Integer, ArrayList<Tuple2<Integer,Double>>> data){
		int rowSize = data._2.size();
		double[] rowValues = new double[rowSize];
		for(int i = 0; i <rowSize; i++){
			rowValues[data._2.get(i)._1] = data._2.get(i)._2;
		}
		return new IndexedRow(data._1, Vectors.dense(rowValues).compressed());
	}
	
	/**
	 * Method that inverse a diagonal matrix.
	 * @param M The diagonal matrix.
	 * @return The inverse of the diagonal matrix.
	 */
	public static IndexedRowMatrix inverseDiagonalMatrix(IndexedRowMatrix M){
		JavaRDD<IndexedRow> inverse = M.rows().toJavaRDD().map(row -> {
			int exactIndex = toIntExact(row.index());
			double[] data = row.vector().toArray();
			if(exactIndex <= data.length)
				data[exactIndex] = 1.0d / data[exactIndex];
			return new IndexedRow(row.index(), Vectors.dense(data));
			});
		return new IndexedRowMatrix(inverse.rdd());
	}
	
	/**
	 * Method that applies the hard thresholding operator, i.e. keeping only the top r eigen value, the rest is set to zero.
	 * @param M The matrix to hard threshold.
	 * @param r The hard threshold.
	 * @return The hard thresholded matrix.
	 */
	public static IndexedRowMatrix hardThreshold(IndexedRowMatrix M, int r){
		JavaRDD<IndexedRow> hardThresholdedMatrix = M.rows().toJavaRDD().map(row -> {
			int exactIndex = toIntExact(row.index());
			double[] data = row.vector().toArray();
			if(exactIndex < r && exactIndex <= data.length)
				data[exactIndex] = 0.0d;
			return new IndexedRow(row.index(), Vectors.dense(data));
			});
		
		return new IndexedRowMatrix(hardThresholdedMatrix.rdd());
	}
	
	public static IndexedRowMatrix multiplicateByLeftDiagonalMatrix(Vector diagMatrix, IndexedRowMatrix mat){
		final double[] diagMatValues = diagMatrix.toArray();
		JavaRDD<IndexedRow> result = mat.rows().toJavaRDD().map(row -> {
			double[] rowValues = row.vector().toArray();
			for(int i = 0; i < rowValues.length; i++){
				rowValues[i] = rowValues[i] * diagMatValues[toIntExact(row.index())];
			}
			return new IndexedRow(row.index(), Vectors.dense(rowValues));
		});
		return new IndexedRowMatrix(result.rdd());
	}
	
	public static IndexedRowMatrix multiplicateByRightDiagonalMatrix(IndexedRowMatrix mat, Vector diagMatrix){
		final double[] diagMatValues = diagMatrix.toArray();
		JavaRDD<IndexedRow> result = mat.rows().toJavaRDD().map(row -> {
			double[] rowValues = row.vector().toArray();
			for(int i = 0; i < rowValues.length; i++){
				rowValues[i] = rowValues[i] * diagMatValues[i];
			}
			return new IndexedRow(row.index(), Vectors.dense(rowValues));
		});
		return new IndexedRowMatrix(result.rdd());
	}
}
