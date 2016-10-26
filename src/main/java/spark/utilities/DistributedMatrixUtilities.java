package spark.utilities;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import scala.Tuple2;
import scala.Tuple3;

/**
 * Class that offers multiple utility function on mlllib distributed matrix object.
 * @author Jonathan Bergeron
 *
 */
public class DistributedMatrixUtilities {
	
	
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
}
