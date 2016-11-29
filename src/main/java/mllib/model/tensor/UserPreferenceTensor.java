package mllib.model.tensor;

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
}
