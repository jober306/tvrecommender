package model.tensor;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.spark.mllib.linalg.Vector;

/**
 * Class that represents an user preference tensor. The user preference tensor p_ufs is the total number
 * of minutes user u spent watching a program with features f in time slot s.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensor implements Serializable{
	
	private static final long serialVersionUID = 1L;

	final UserPreference userPreference;
	
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
	public UserPreferenceTensor(UserPreference userPreference){
		this.userPreference = userPreference;
		this.totalWatchTime = 0;
	}
	
	/**
	 * Setter method that increment the value of total watch time by the given amount.
	 * @param watchTime The amount of time that will be added to the total watch time.
	 */
	public void incrementValue(int watchTime){
		totalWatchTime += watchTime;
	}
	
	/**
	 * Getter method that returns the user id of this tensor.
	 * @return The user id of this tensor.
	 */
	public int userId(){
		return this.userPreference.userId();
	}
	
	/**
	 * Getter method that returns the program feature vector of this tensor.
	 * @return The program feature vector of this tensor.
	 */
	public Vector programFeatureVector(){
		return this.userPreference.programFeatureVector();
	}
	
	/**
	 * Getter method that returns the slot of this tensor.
	 * @return The slot of this tensor.
	 */
	public int slot(){
		return this.userPreference.slot();
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
