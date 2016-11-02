package spark.utilities;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.sql.catalyst.expressions.aggregate.Final;
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
	
	/**
	 * Method that applies the hard thresholding operator, i.e. keeping only the top r eigen value, the rest is set to zero.
	 * @param v The vector to hard threshold.
	 * @param r The hard threshold.
	 * @return The hard thresholded vector.
	 */
	public static Vector hardThreshold(Vector v, int r){
		double[] values = v.toArray();
		for(int i = r; i < values.length; i++){
			values[i] = 0.0d;
		}
		return Vectors.dense(values).compressed();
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
	
	public static double scalarProduct(double[] v1, double[] v2){
		double total = 0;
		for(int i = 0; i < v1.length; i++){
			total += v1[i] * v2[i];
		}
		return total;
	}
	
	public static double scalarProduct(Vector v1, Vector v2){
		return scalarProduct(v1.toArray(), v2.toArray());
	}
	
	public static Matrix toSparseLocalMatrix(IndexedRowMatrix mat){
		List<Tuple3<Integer, Integer, Double>> triplets = mat.rows().toJavaRDD().flatMap(row -> extractTripletFromRow(row)).collect();
		int[] rowIndices = new int[triplets.size()];
		int[] colIndices = new int[triplets.size()];
		double[] values = new double[triplets.size()];
		for(int i = 0 ; i < triplets.size(); i++){
			rowIndices[i] = triplets.get(i)._1();
			colIndices[i] = triplets.get(i)._2();
			values[i] = triplets.get(i)._3();
		}
		return Matrices.sparse(toIntExact(mat.numRows()), toIntExact(mat.numCols()), rowIndices, colIndices, values);
	}
	
	public static Vector multiplyVectorByMatrix(Vector vec, IndexedRowMatrix mat){
		final double[] vecValues = vec.toArray();
		List<Double> results = mat.rows().toJavaRDD().mapToDouble(row -> scalarProduct(vecValues, row.vector().toArray())).collect();
		return Vectors.dense(ArrayUtils.toPrimitive(results.toArray(new Double[results.size()]))).compressed();
	}
	
	public static Vector indexedRowToVector(IndexedRow row){
		return Vectors.dense(row.vector().toArray());
	}
	
	private static Iterator<Tuple3<Integer, Integer,Double>> extractTripletFromRow(IndexedRow row){
		List<Tuple3<Integer, Integer, Double>> triplets = new ArrayList<Tuple3<Integer, Integer, Double>>();
		int rowIndex = toIntExact(row.index());
		SparseVector sparceRow = row.vector().toSparse();
		int[] indices = sparceRow.indices();
		double[] values = sparceRow.values();
		for(int i = 0; i < indices.length; i++){
			triplets.add(new Tuple3<Integer, Integer, Double>(rowIndex, indices[i], values[i]));
		}
		return triplets.iterator();
	}
}
