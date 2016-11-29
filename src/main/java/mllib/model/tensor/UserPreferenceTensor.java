package mllib.model.tensor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.spark.mllib.linalg.Vector;

public class UserPreferenceTensor {
	
	int userId;
	Vector programFeatureVector;
	short slot;
	
	int totalWatchTime;
	
	public UserPreferenceTensor(int userId, Vector programFeatureVector, short slot){
		this.userId = userId;
		this.programFeatureVector = programFeatureVector;
		this.slot = slot;
		this.totalWatchTime = 0;
	}
	
	public void IncrementValue(int watchTime){
		totalWatchTime += watchTime;
	}
	
	public int getUserId(){
		return userId;
	}
	
	public Vector getProgramFeatureVector(){
		return programFeatureVector;
	}
	
	public short getSlot(){
		return slot;
	}
	
	@Override
    public int hashCode() {
		return new HashCodeBuilder(17,31).append(userId).append(programFeatureVector.toArray()).append(slot).hashCode();
	}
	
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
}
