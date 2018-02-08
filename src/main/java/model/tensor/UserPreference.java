package model.tensor;

import java.io.Serializable;

import org.apache.spark.mllib.linalg.Vector;

import scala.Tuple3;

/**
 * Class holding information about the user preference id.
 * @author Jonathan Bergeron
 *
 */
public class UserPreference implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The user of this tensor.
	 */
	final int userId;
	
	/**
	 * The program feature vector representing the program of this tensor.
	 */
	final Vector programFeatureVector;
	
	/**
	 * The time slot of this tensor.
	 */
	final int slot;

	public UserPreference(int userId, Vector programFeatureVector, int slot){
		this.userId = userId;
		this.programFeatureVector = programFeatureVector;
		this.slot = slot;
	}
	
	public UserPreference(Tuple3<Integer, Vector, Integer> userPref){
		this.userId = userPref._1();
		this.programFeatureVector = userPref._2();
		this.slot = userPref._3();
	}
	
	/**
	 * 
	 * @return The user id of this user preference.
	 */
	public int userId() {
		return userId;
	}

	/**
	 * 
	 * @return The program feature vector of this user preference.
	 */
	public Vector programFeatureVector() {
		return programFeatureVector;
	}

	/**
	 * 
	 * @return return the time slot of this user preference.
	 */
	public int slot() {
		return slot;
	}
}
