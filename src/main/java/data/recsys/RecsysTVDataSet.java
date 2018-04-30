package data.recsys;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;

import org.apache.spark.api.java.JavaRDD;

import data.TVDataSet;
import model.data.User;

/**
 * Class that represents a data set of recsys tv event. The class holds the
 * spark context in which it has been loaded and offer multiple functionalities
 * on the data set.
 * 
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSet extends TVDataSet<User, RecsysTVProgram, RecsysTVEvent> implements Serializable{

	/**
	 * The date the data set started recording tv audience behavior (This is not
	 * the real one). Monday, april 1995 at midnigh.
	 */
	public static final LocalDateTime START_TIME = LocalDateTime.of(1995,
			Month.APRIL, 10, 0, 0);

	private static final long serialVersionUID = 1L;

	/**
	 * Main constructor of the class. Use the
	 * <class>RecsysTVDataSetLoader</class> to get the RDD off a csv file.
	 * 
	 * @param eventsData
	 *            The data in spark RDD format.
	 * @param sc
	 *            The java spark context in which the data has been loaded.
	 */
	public RecsysTVDataSet(JavaRDD<RecsysTVEvent> eventsData) {
		super(eventsData);
	}
}
