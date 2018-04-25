package data;

import static util.time.TimeUtilities.isDateTimeBetween;

import java.io.Serializable;
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
 * @param <T>
 *            The child class of TVProgram that this EPG will contain.
 */
public class EPG<T extends TVProgram> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The java rdd containing all the tv program.
	 */
	transient protected JavaRDD<T> electronicProgrammingGuide;

	/**
	 * The spark context used to load the EPG.
	 */
	transient protected JavaSparkContext sc;

	/**
	 * Abstract constructor that simply initialize the parameter
	 * 
	 * @param electronicProgrammingGuide
	 */
	public EPG(JavaRDD<T> electronicProgrammingGuide, JavaSparkContext sc) {
		this.electronicProgrammingGuide = electronicProgrammingGuide;
		this.sc = sc;
	}
	
	public void cache(){
		electronicProgrammingGuide = electronicProgrammingGuide.cache();
	}

	/**
	 * Getter method that returns the java rdd containing tv program
	 * representing this EPG.
	 * 
	 * @return The java rdd containing tv program of this EPG.
	 */
	public JavaRDD<T> getEPG() {
		return electronicProgrammingGuide;
	}

	/**
	 * Method that returns the tv program broadcasted at given watch time. i.e
	 * the watch time is between the start time and the end time of the program.
	 * 
	 * @param targetWatchTime
	 *            The target watch time.
	 * @return The java rdd of tv programs occurring during target watch time.
	 */
	public JavaRDD<T> getJavaRDDProgramsAtWatchTime(
			LocalDateTime targetWatchTime) {
		return electronicProgrammingGuide.filter(program -> isDateTimeBetween(
				program.startTime(), program.endTime(), targetWatchTime));
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
	public JavaRDD<T> getJavaRDDProgramsBetweenTimes(
			LocalDateTime startTargetTime, LocalDateTime endTargetTime) {
		return electronicProgrammingGuide.filter(program -> startTargetTime
				.isBefore(program.endTime())
				&& endTargetTime.isAfter(program.startTime()));
	}

	/**
	 * Method that returns the tv program broadcasted at given watch time. i.e
	 * the watch time is between the start time and the end time of the program.
	 * 
	 * @param targetWatchTime
	 *            The target watch time.
	 * @return The list of all tv programs occurring during target watch time.
	 */
	public List<T> getListProgramsAtWatchTime(LocalDateTime targetWatchTime) {
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
	public List<T> getListProgramsBetweenTimes(LocalDateTime startTargetTime,
			LocalDateTime endTargetTime) {
		return getJavaRDDProgramsBetweenTimes(startTargetTime, endTargetTime)
				.collect();
	}
}
