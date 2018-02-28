package model.tensor;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
	final short slot;

	public UserPreference(int userId, Vector programFeatureVector, short slot){
		this.userId = userId;
		this.programFeatureVector = programFeatureVector;
		this.slot = slot;
	}
	
	public UserPreference(Tuple3<Integer, Vector, Short> userPref){
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
	public short slot() {
		return slot;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("User Id: ");
		sb.append(userId);
		sb.append("|Program Features: ");
		sb.append(Arrays.toString(programFeatureVector.toArray()));
		sb.append("|Slot: ");
		sb.append(slot);
		return sb.toString();
	}
	
	/**
	 * Override of the equals method. Two user preference tensor are considered equals if and only
	 * if the object is a user preference tensor and if the user id, all the entries of the program
	 * feature vector and the slot are equals.
	 * @param obj The object on which equality will be verified.
	 * @return True if the object is equals to this tensor, false otherwise.
	 */
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof UserPreference))
            return false;
        if (obj == this)
            return true;

        UserPreference rhs = (UserPreference) obj;
        return new EqualsBuilder().
            append(userId(), rhs.userId()).
            append(programFeatureVector().toArray(), rhs.programFeatureVector().toArray()).
            append(slot(), rhs.slot()).
            isEquals();
	}
	
	@Override
    public int hashCode() {
		return new HashCodeBuilder(17,31).append(userId()).append(programFeatureVector().toArray()).append(slot()).hashCode();
	}
}
