package data.recsys.loader;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import scala.Tuple2;
import util.spark.SparkUtilities;

/**
 * Class used to load data in the form of the recsys tv data set. The loader is
 * expecting a csv with attributes in the following order:
 * ChannelID,Slot,Week,GenreID,SubgenreID,userID,programID,eventID,duration
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSetLoader {

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
		sc = SparkUtilities.getADefaultSparkContext();
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
		sc = SparkUtilities.getADefaultSparkContext();
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
	 * Main method of the class. Used to load the recsys tv events from the
	 * specified file location. The EPG is derived implicitly from the events.
	 * 
	 * @return A tuple 2 containing in its first argument the EPG and the events
	 *         in the other.
	 */
	public Tuple2<RecsysEPG, RecsysTVDataSet> loadDataSet() {
		JavaRDD<RecsysTVEvent> events = linesToTVEvent(loadLinesFromDataSet());
		JavaRDD<RecsysTVProgram> programs = createProgramsImplicitlyFromEvents(events);
		RecsysTVDataSet tvDataSet = new RecsysTVDataSet(events);
		RecsysEPG epg = new RecsysEPG(programs);
		return new Tuple2<RecsysEPG, RecsysTVDataSet>(epg, tvDataSet);
	}

	/**
	 * Main method of the class. Used to load the recsys tv events from the
	 * specified file location. The EPG is derived implicitly from the events.
	 * 
	 * @param minDuration
	 *            The min duration a tv event must have to be added into the
	 *            data set.
	 * 
	 * @return A tuple 2 containing in its first argument the EPG and the events
	 *         in the other.
	 */
	public Tuple2<RecsysEPG, RecsysTVDataSet> loadDataSet(int minDuration) {
		JavaRDD<RecsysTVEvent> events = linesToTVEvent(loadLinesFromDataSet());
		JavaRDD<RecsysTVProgram> programs = createProgramsImplicitlyFromEvents(events);
		RecsysTVDataSet tvDataSet = new RecsysTVDataSet(events);
		RecsysTVDataSet filteredTVDataSet = (RecsysTVDataSet) tvDataSet.filterByMinDuration(minDuration);
		RecsysEPG epg = new RecsysEPG(programs);
		return new Tuple2<RecsysEPG, RecsysTVDataSet>(epg, filteredTVDataSet);
	}

	/**
	 * Method that returns the java spark context used to load the data set.
	 * 
	 * @return The java spark context used to load the data set.
	 */
	public JavaSparkContext getJavaSparkContext() {
		return sc;
	}

	/**
	 * Method that read all the lines of the data set to put it into a RDD.
	 * 
	 * @return a JavaRDD of <class>String</class>.
	 */
	private JavaRDD<String> loadLinesFromDataSet() {
		String path_s = RecsysTVDataSetLoader.class.getResource(pathToDataSet).getPath();
		return sc.textFile(path_s);
	}

	/**
	 * Method that implicitly creates from all the events the corresponding
	 * programs. For example if a user listen to program 2 on channel 3 on slot
	 * 10 and another one listen to the same program on slot 11, a program will
	 * be created with start time from slot 10 to 11. Thus the programs returned
	 * do not form a continuous sequence of programs.
	 * 
	 * @param events
	 *            The tv events.
	 * @return The program implicitly created from the events.
	 */
	private JavaRDD<RecsysTVProgram> createProgramsImplicitlyFromEvents(JavaRDD<RecsysTVEvent> events) {
		return events.map(event -> event.program()).distinct();
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
	public static JavaRDD<RecsysTVEvent> linesToTVEvent(JavaRDD<String> lines) {
		return lines.map(line -> toTVEvent(line));
	}

	/**
	 * Method that map a single line to a Recsys tv event.
	 * 
	 * @param line
	 *            The String representing the Recsys tv event in csv format.
	 * @return The <class> RecsysTVEvent<\class> object representing the line.
	 */
	public static RecsysTVEvent toTVEvent(String line) {
		String[] row = line.split(",");
		return new RecsysTVEvent(Short.parseShort(row[0]),
				Short.parseShort(row[1]), Byte.parseByte(row[2]),
				Byte.parseByte(row[3]), Byte.parseByte(row[4]),
				Integer.parseInt(row[5]), Integer.parseInt(row[6]),
				Integer.parseInt(row[7]), Integer.parseInt(row[8]));
	}
}
