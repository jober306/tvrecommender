package mllib.model.tensor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

/**
 * Class that represents a collection of user preference tensor that extends hash map. The main advantage is when using
 * the put method the time value of the new user preference tensor will be added if one was already existing with same
 * user id, program feature vector and slot. It also offers utilities method.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollection {
	
	public static final int ANY = -1;
	
	public static Vector getAnyFeatureVector(int size){
		double[] features = new double[size];
		Arrays.fill(features, ANY);
		return Vectors.dense(features);
	}
	
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
	
	public List<UserPreferenceTensor> getAllUserPreferenceTensors(){
		return syncMap.entrySet().stream().map(Entry::getValue).collect(Collectors.toList());
	}
	
	/**
	 * Method that returns all the user preference tensors with the corresponding user id,
	 * program feature vectors and slot. It is also possible to give the
	 * UserPreferenceTensorCollection.ANY value to any of the arguments, including every
	 * value in the program feature vector. For example if the ANY value is given for user
	 * id, the returned list will contains tensors corresponding to the given slot and program
	 * feature vector for any users.
	 * @param userId The wanted user id.
	 * @param programFeatures The wanted program feature vector.
	 * @param slot The wanted slot.
	 * @return A list containing all the user preference tensors corresponding to the given attributes.
	 */
	public List<UserPreferenceTensor> getUserPreferenceTensors(int userId, Vector programFeatures, int slot){
		final double[] wantedProgramFeatures = programFeatures.toArray();
		return syncMap.entrySet().stream().filter(entry -> {
			UserPreferenceTensor tensor = entry.getValue();
			if(userId != ANY && userId != tensor.getUserId()){
				return false;
			}
			double[] tensorFeatures = tensor.getProgramFeatureVector().toArray();
			for(int i = 0; i < tensorFeatures.length; i++){
				double feature = tensorFeatures[i];
				double wantedFeature = wantedProgramFeatures[i];
				if(wantedFeature != ANY && wantedFeature != feature){
					return false;
				}
			}
			if(slot != ANY && slot != tensor.getSlot()){
				return false;
			}
			return true;
		}).map(Entry::getValue).collect(Collectors.toList());
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
