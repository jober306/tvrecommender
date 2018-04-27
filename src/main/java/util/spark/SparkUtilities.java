package util.spark;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Class that offers some utility method to use spark.
 * @author Jonathan Bergeron
 *
 */
public class SparkUtilities {
	
	/**
	 * Method that transforms a single element into a spark RDD.
	 * @param element An object.
	 * @param sc The java spark context in which the element will be loaded.
	 * @return The RDD associated with this element.
	 */
	public static <T> JavaRDD<T> elementToJavaRDD(T element, JavaSparkContext sc){
		JavaRDD<T> elementRDD = sc.parallelize(Arrays.asList(element));
		return elementRDD;
	}
	
	/**
	 * Method that transforms a list of elements into a spark RDD.
	 * @param elements A list of object.
	 * @param sc The java spark context in which the elements will be loaded.
	 * @return The RDD associated with this element.
	 */
	public static<T> JavaRDD<T> elementsToJavaRDD(List<T> elements, JavaSparkContext sc){
		return sc.parallelize(elements);
	}
	
	/**
	 * Method that return the default java spark context i.e. the spark operations are local
	 * and are using all available core.
	 * @return The default java spark context.
	 */
	public static JavaSparkContext getADefaultSparkContext(){
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("App with default spark context");
		JavaSparkContext sc = new JavaSparkContext(conf);
		sc.setLogLevel("WARN");
		return sc;
	}
}
