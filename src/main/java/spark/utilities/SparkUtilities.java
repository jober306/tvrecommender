package spark.utilities;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkUtilities {
	
	
	public static <T> JavaRDD<T> elementToJavaRDD( T element, JavaSparkContext sc){
		JavaRDD<T> elementRDD = sc.parallelize(Arrays.asList(element));
		return elementRDD;
	}
	
	public static<T> JavaRDD<T> elementsToJavaRDD(List<T> elements, JavaSparkContext sc){
		return sc.parallelize(elements);
	}
	
	public static JavaSparkContext getADefaultSparkContext(){
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("App with default spark context");
		return new JavaSparkContext(conf);
	}
}
