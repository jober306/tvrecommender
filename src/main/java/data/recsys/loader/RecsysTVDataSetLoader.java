package data.recsys.loader;

import static data.recsys.loader.RecsysTVDataSetLoaderUtilities.linesToTVEvent;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;
import util.SparkUtilities;
import util.TVDataSetUtilities;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;

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
		RecsysTVDataSet tvDataSet = new RecsysTVDataSet(events, sc);
		RecsysEPG epg = new RecsysEPG(programs, sc);
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
		JavaRDD<RecsysTVEvent> filteredEvents = TVDataSetUtilities
				.filterByMinDuration(events, 5);
		JavaRDD<RecsysTVProgram> programs = createProgramsImplicitlyFromEvents(filteredEvents);
		RecsysTVDataSet tvDataSet = new RecsysTVDataSet(events, sc);
		RecsysEPG epg = new RecsysEPG(programs, sc);
		return new Tuple2<RecsysEPG, RecsysTVDataSet>(epg, tvDataSet);
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
		String path_s = RecsysTVDataSetLoader.class.getResource(pathToDataSet)
				.getPath();
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
	private JavaRDD<RecsysTVProgram> createProgramsImplicitlyFromEvents(
			JavaRDD<RecsysTVEvent> events) {
		return events.map(
				event -> new RecsysTVProgram(event.getWeek(), event.getSlot(),
						event.getChannelId(), event.getProgramId(), event
								.getGenreID(), event.getSubgenreID()))
				.distinct();
	}

	public static void main(String[] args) {
		RecsysTVDataSetLoader l = new RecsysTVDataSetLoader();
		Tuple2<RecsysEPG, RecsysTVDataSet> data = l.loadDataSet();
		System.out.println(data._1.getEPG().count());
		System.out.println(data._2.getEventsData().count());
	}
}
