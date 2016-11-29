package mllib.model.tensor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class that represents a collection of user preference tensor that extends hash map. The main advantage is when using
 * the put method the time value of the new user preference tensor will be added if one was already existing with same
 * user id, program feature vector and slot. It also offers utilities method.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollection {
	
	Map<UserPreferenceTensor,UserPreferenceTensor> syncMap;
	
	/**
	 * Default constructor that build an empty synchronized HashMap of UserPreferenceTensor.
	 */
	public UserPreferenceTensorCollection(){
		syncMap = Collections.synchronizedMap(new HashMap<UserPreferenceTensor,UserPreferenceTensor>());
	}
	
	public UserPreferenceTensorCollection(UserPreferenceTensorCollection c){
		syncMap = Collections.synchronizedMap(c.getSyncMap());
	}
	
	private Map<UserPreferenceTensor,UserPreferenceTensor> getSyncMap(){
		return syncMap;
	}
	
	public void put(UserPreferenceTensor key, UserPreferenceTensor value){
		if(syncMap.containsKey(key)){
			syncMap.get(key).IncrementValue(value.getTotalWatchTime());
		}else{
			syncMap.put(key, value);
		}
	}
	
	public void putAll(UserPreferenceTensorCollection collection){
		for(Entry<UserPreferenceTensor,UserPreferenceTensor> entry :  collection.getSyncMap().entrySet()){
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public boolean isEmpty(){
		return syncMap.isEmpty();
	}
	
}
