package mllib.model.tensor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class that represents a collection of user preference tensor that extends hash map. The main advantage is when using
 * the put method the time value of the new user preference tensor will be added if one was already existing with same
 * user id, program feature vector and slot. It also offers utilities method.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollection {
	
	/**
	 * The synchronised map containing the user preference tensor. The total watch time of a specific
	 * tensor is held in the value of the map. 
	 */
	Map<UserPreferenceTensor,UserPreferenceTensor> syncMap;
	
	/**
	 * Default constructor that build an empty synchronized HashMap of UserPreferenceTensor.
	 */
	public UserPreferenceTensorCollection(){
		syncMap = Collections.synchronizedMap(new HashMap<UserPreferenceTensor,UserPreferenceTensor>());
	}
	
	/**
	 * Copy constructor that make a deep copy of the given user preference tensor collection.
	 * @param c The user preference tensor collection to copy.
	 */
	public UserPreferenceTensorCollection(UserPreferenceTensorCollection c){
		syncMap = Collections.synchronizedMap(c.getSyncMap());
	}
	
	/**
	 * Method to add the watching time of this tensor to the corresponding tensor in the map. If
	 * it does not exist already in the map it is simply added to it.
	 * @param tensor The tensor to add to the collection.
	 */
	public void add(UserPreferenceTensor tensor){
		if(syncMap.containsKey(tensor)){
			syncMap.get(tensor).incrementValue(tensor.getTotalWatchTime());
		}else{
			syncMap.put(tensor, tensor);
		}
	}
	
	/**
	 * Method that add a whole collection of user preference tensors. It simply call the
	 * add method for all the tensors in the given collection.
	 * @param collection The user preference tensors to add to this collection.
	 */
	public void addAll(UserPreferenceTensorCollection collection){
		for(Entry<UserPreferenceTensor,UserPreferenceTensor> entry :  collection.getSyncMap().entrySet()){
			add(entry.getValue());
		}
	}
	
	/**
	 * Method that checks if the collection is emtpy or not.
	 * @return True if the collection is empty, false otherwise.
	 */
	public boolean isEmpty(){
		return syncMap.isEmpty();
	}
	
	private Map<UserPreferenceTensor,UserPreferenceTensor> getSyncMap(){
		return syncMap;
	}
	
}
