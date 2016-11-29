package mllib.model.tensor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.spark.mllib.linalg.Vector;

/**
 * Class that represents an user preference tensor. The user preference tensor p_ufs is the total number
 * of minutes user u spent watching a program with features f in time slot s.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensor {
	
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
	
	/**
	 * Total watch time for this particular user, program feature vector and slot.
	 */
	int totalWatchTime;
	
	/**
	 * Constructor of the 
	 * @param userId The program feature vector representing the program of this tensor.
	 * @param programFeatureVector The program feature vector representing the program of this tensor.
	 * @param slot The time slot of this tensor.
	 */
	public UserPreferenceTensor(int userId, Vector programFeatureVector, short slot){
		this.userId = userId;
		this.programFeatureVector = programFeatureVector;
		this.slot = slot;
		this.totalWatchTime = 0;
	}
	
	/**
	 * Setter method that increment the value of total watch time by the given amount.
	 * @param watchTime The amount of time that will be added to the total watch time.
	 */
	public void IncrementValue(int watchTime){
		totalWatchTime += watchTime;
	}
	
	/**
	 * Getter method that returns the user id of this tensor.
	 * @return The user id of this tensor.
	 */
	public int getUserId(){
		return userId;
	}
	
	/**
	 * Getter method that returns the program feature vector of this tensor.
	 * @return The program feature vector of this tensor.
	 */
	public Vector getProgramFeatureVector(){
		return programFeatureVector;
	}
	
	/**
	 * Getter method that returns the slot of this tensor.
	 * @return The slot of this tensor.
	 */
	public short getSlot(){
		return slot;
	}
	
	/**
	 * Getter method that returns the total watch time of this tensor.
	 * @return The total watch time of this tensor.
	 */
	public int getTotalWatchTime() {
		return totalWatchTime;
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
		if (!(obj instanceof UserPreferenceTensor))
            return false;
        if (obj == this)
            return true;

        UserPreferenceTensor rhs = (UserPreferenceTensor) obj;
        return new EqualsBuilder().
            append(userId, rhs.getUserId()).
            append(programFeatureVector.toArray(), rhs.getProgramFeatureVector().toArray()).
            append(slot, rhs.getSlot()).
            isEquals();
	}
	
	@Override
    public int hashCode() {
		return new HashCodeBuilder(17,31).append(userId).append(programFeatureVector.toArray()).append(slot).hashCode();
	}
}
