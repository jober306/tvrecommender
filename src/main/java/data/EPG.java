package data;

import static util.time.TimeUtilities.isDateTimeBetween;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import model.data.TVProgram;

/**
 * Abstract class that represents an electronic programming guide (EPG). It
 * contains a JavaRDD of the given TVProgram child class.
 * 
 * @author Jonathan Bergeron
 *
 * @param <P>
 *            The child class of TVProgram that this EPG will contain.
 */
public class EPG<P extends TVProgram> {

	/**
	 * The java rdd containing all the tv programs.
	 */
	transient protected JavaRDD<P> tvPrograms;

	/**
	 * The spark context used to load the EPG.
	 */
	transient protected JavaSparkContext sc;

	/**
	 * Abstract constructor that simply initialize the parameter
	 * 
	 * @param tvPrograms
	 */
	public EPG(JavaRDD<P> tvPrograms) {
		this.tvPrograms = tvPrograms;
		this.sc = new JavaSparkContext(tvPrograms.context());
	}
	
	public void cache(){
		tvPrograms.cache();
	}

	/**
	 * Getter method that returns the java rdd containing tv program
	 * representing this EPG.
	 * 
	 * @return The java rdd containing tv program of this EPG.
	 */
	public JavaRDD<P> tvPrograms() {
		return tvPrograms;
	}

	/**
	 * Method that returns the tv program broadcasted at given watch time. i.e
	 * the watch time is between the start time and the end time of the program.
	 * 
	 * @param targetWatchTime
	 *            The target watch time.
	 * @return The java rdd of tv programs occurring during target watch time.
	 */
	public JavaRDD<P> getJavaRDDProgramsAtWatchTime(LocalDateTime targetWatchTime) {
		return tvPrograms.filter(program -> isDateTimeBetween(program.startTime(), program.endTime(), targetWatchTime));
	}

	/**
	 * Method that returns the tv program broadcasted between the start and end
	 * time.
	 * 
	 * @param startTargetTime
	 *            The start target time
	 * @param endTargetTime
	 *            The end target time.
	 * @return The java rdd of tv programs occurring during target watch time.
	 */
	public JavaRDD<P> getJavaRDDProgramsBetweenTimes(LocalDateTime startTargetTime, LocalDateTime endTargetTime) {
		return tvPrograms.filter(program -> startTargetTime.isBefore(program.endTime()) && endTargetTime.isAfter(program.startTime()));
	}

	/**
	 * Method that returns the tv program broadcasted at given watch time. i.e
	 * the watch time is between the start time and the end time of the program.
	 * 
	 * @param targetWatchTime
	 *            The target watch time.
	 * @return The list of all tv programs occurring during target watch time.
	 */
	public List<P> getListProgramsAtWatchTime(LocalDateTime targetWatchTime) {
		return getJavaRDDProgramsAtWatchTime(targetWatchTime).collect();
	}

	/**
	 * Method that returns programs that occurs between start and end target
	 * time. The program may have started before the start target time or may
	 * end after the end target time.
	 * 
	 * @param startTargetTime
	 *            The start target time.
	 * @param endTargetTime
	 *            The end target time.
	 * @return The list of program indexes that occurs between start and end
	 *         target time.
	 */
	public List<P> getListProgramsBetweenTimes(LocalDateTime startTargetTime, LocalDateTime endTargetTime) {
		return getJavaRDDProgramsBetweenTimes(startTargetTime, endTargetTime).collect();
	}
}
