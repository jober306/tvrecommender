package data.recsys.loader;

import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;

/**
 * Class used to load data in the form of the recsys tv data set. The loader is
 * expecting a csv with attributes in the following order:
 * ChannelID,Slot,Week,GenreID,SubgenreID,userID,programID,eventID,duration
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSetLoader implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The default location of the recsys tv data set.
	 */
	public final static String DEFAULT_DATASET_LOCATION = "/tv-audience-dataset/tv-audience-dataset.csv";

	/**
	 * The current path to the recsys tv data set. This path is used when the
	 * <method>loadDataSet</method> is called.
	 */
	private String pathToDataSet;

	/**
	 * The spark context used to load the data.
	 */
	transient JavaSparkContext sc;

	/**
	 * Default Constructor of the class using the default data set location and
	 * a default spark context.
	 */
	public RecsysTVDataSetLoader() {
		pathToDataSet = DEFAULT_DATASET_LOCATION;
		initializeSparkContext();
	}

	/**
	 * Constructor with a specific path to the data set and the default spark
	 * context.
	 * 
	 * @param pathToDataSet
	 *            the path to the data set.
	 */
	public RecsysTVDataSetLoader(String pathToDataSet) {
		this.pathToDataSet = pathToDataSet;
		initializeSparkContext();
	}

	/**
	 * Constructor with a specific path to the data set and a specific spark
	 * context.
	 * 
	 * @param pathToDataSet
	 *            the path to the data set.
	 * @param sc
	 *            the spark context used when loading data.
	 */
	public RecsysTVDataSetLoader(String pathToDataSet, JavaSparkContext sc) {
		this.pathToDataSet = pathToDataSet;
		this.sc = sc;
	}

	/**
	 * Constructor using the default path to the data set and a specific spark
	 * context.
	 * 
	 * @param sc
	 *            the spark context used when loading data.
	 */
	public RecsysTVDataSetLoader(JavaSparkContext sc) {
		pathToDataSet = DEFAULT_DATASET_LOCATION;
		this.sc = sc;
	}

	/**
	 * Method that initializes the default spark context. This spark context is
	 * local and is using all available hearts of the machine. The application
	 * is also named "Recsys TV Data Set Loader".
	 */
	private void initializeSparkContext() {
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(
				"Recsys TV Data Set Loader");
		sc = new JavaSparkContext(conf);
	}

	/**
	 * Main method of the class. Used to load the data set all at once and
	 * return it in a RDD of <class>RecsysTVEvent</class>.
	 * 
	 * @return A JavaRDD of <class>RecsysTVEvent</class>.
	 */
	public RecsysTVDataSet loadDataSet() {
		JavaRDD<RecsysTVEvent> events = mapLinesToTVEvent(loadLinesFromDataSet());
		return new RecsysTVDataSet(events, sc);
	}

	/**
	 * Method that read all the lines of the data set to put it into a RDD.
	 * 
	 * @return a JavaRDD of <class>String</class>.
	 */
	private JavaRDD<String> loadLinesFromDataSet() {
		String path_s = RecsysTVDataSetLoader.class.getResource(pathToDataSet)
				.getPath();
		return sc.textFile(path_s);
	}

	/**
	 * Method that map all lines in a RDD to a RDD of
	 * <class>RecsysTVEvent</class>.
	 * 
	 * @param lines
	 *            A JavaRDD of <class>String</class> containing the recsys tv
	 *            event in csv format.
	 * @return A JavaRDD of <class>RecsysTVEvent</class>.
	 */
	private JavaRDD<RecsysTVEvent> mapLinesToTVEvent(JavaRDD<String> lines) {
		return lines.map(line -> mapLineToTVEvent(line));
	}

	/**
	 * Method that map a single line to a Recsys tv event.
	 * 
	 * @param line
	 *            The String representing the Recsys tv event in csv format.
	 * @return The <class> RecsysTVEvent<\class> object representing the line.
	 */
	private RecsysTVEvent mapLineToTVEvent(String line) {
		String[] row = line.split(",");
		return new RecsysTVEvent(Short.parseShort(row[0]),
				Short.parseShort(row[1]), Byte.parseByte(row[2]),
				Byte.parseByte(row[3]), Byte.parseByte(row[4]),
				Integer.parseInt(row[5]), Integer.parseInt(row[6]),
				Integer.parseInt(row[7]), Integer.parseInt(row[8]));
	}
}
