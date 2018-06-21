package util.spark;

import java.util.Arrays;
import java.util.List;

import org.apache.ivy.ant.IvyMakePom.Mapping;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.feature.RecsysBooleanFeatureExtractor;
import data.recsys.feature.RecsysFeatureExtractor;
import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.data.feature.ChannelFeatureExtractor;
import model.data.feature.FeatureExtractor;
import model.data.mapping.AbstractMapping;
import model.data.mapping.IdentityMapping;
import model.data.mapping.TVProgramIDMapping;
import model.data.mapping.TVProgramMapping;
import model.data.mapping.UserIDMapping;
import model.data.mapping.UserMapping;
import model.tensor.UserPreference;
import model.tensor.UserPreferenceTensor;
import model.tensor.UserPreferenceTensorCollection;
import model.tensor.UserPreferenceTensorCollectionAccumulator;
import util.function.SerializableFunction;
import util.function.SerializableSupplier;
import util.time.LocalDateTimeDTO;

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
		SparkConf conf = new SparkConf()
				.setMaster("local[*]")
				.setAppName("TV Recommender")
				.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
				.set("spark.kryoserializer.buffer.max", "512")
				.registerKryoClasses(classesToSerialize());
		JavaSparkContext sc = new JavaSparkContext(conf);
		sc.setLogLevel("WARN");
		return sc;
	}
	
	private static Class<?>[] classesToSerialize(){
		return new Class<?>[] {
			TVEvent.class, 
			User.class, 
			TVProgram.class, 
			RecsysTVEvent.class, 
			RecsysTVProgram.class,
			FeatureExtractor.class,
			ChannelFeatureExtractor.class,
			RecsysFeatureExtractor.class,
			RecsysBooleanFeatureExtractor.class,
			Mapping.class,
			AbstractMapping.class,
			IdentityMapping.class,
			TVProgramIDMapping.class,
			TVProgramMapping.class,
			UserIDMapping.class,
			UserMapping.class,
			UserPreference.class,
			UserPreferenceTensor.class,
			UserPreferenceTensorCollection.class,
			UserPreferenceTensorCollectionAccumulator.class,
			SerializableFunction.class,
			SerializableSupplier.class,
			LocalDateTimeDTO.class
			};
	}
}
