package data.model;

import static time.utilities.TimeUtilities.isDateTimeBetween;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Abstract class that represents an electronic programming guide (EPG).
 * It is containing a JavaRDD of a given TVProgram child class. 
 * @author Jonathan Bergeron
 *
 * @param <T> The child class of TVProgram that this EPG will contain.
 */
public abstract class EPG<T extends TVProgram> implements Serializable{
	
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
	 * @param electronicProgrammingGuide
	 */
	public EPG(JavaRDD<T> electronicProgrammingGuide, JavaSparkContext sc){
		this.electronicProgrammingGuide = electronicProgrammingGuide;
		this.sc = sc;
	}
	
	/**
	 * Getter method that returns the java rdd containing tv program representing this EPG.
	 * @return The java rdd containing tv program of this EPG.
	 */
	public JavaRDD<T> getEPG(){
		return electronicProgrammingGuide;
	}
	
	/**
	 * Method that returns the tv program broadcasted at given watch time. i.e the watch time is between
	 * the start time and the end time of the program.
	 * @param targetWatchTime The target watch time.
	 * @return The java rdd of tv programs occurring during target watch time.
	 */
	public JavaRDD<T> getJavaRDdProgramsAtWatchTime(LocalDateTime targetWatchTime){
		return electronicProgrammingGuide.filter(program -> isDateTimeBetween(program.getStartTime(), program.getEndTime(), targetWatchTime));
	}
	
	/**
	 * Method that returns the tv program broadcasted at given watch time. i.e the watch time is between
	 * the start time and the end time of the program.
	 * @param targetWatchTime The target watch time.
	 * @return The list of all tv programs occurring during target watch time.
	 */
	public List<T> getListProgramsAtWatchTime(LocalDateTime targetWatchTime){
		return electronicProgrammingGuide.filter(program -> isDateTimeBetween(program.getStartTime(), program.getEndTime(), targetWatchTime)).collect();
	}
}
