package model.tensor;

import java.io.Serializable;
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
 * Class that represents a collection of user preference tensor that extends
 * hash map. The main advantage is when using the put method the time value of
 * the new user preference tensor will be added if one was already existing with
 * same user id, program feature vector and slot. It also offers utilities
 * method.
 * 
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollection implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ANY = -1;
	
	final boolean anyUser;
	final boolean anyProgram;
	final boolean anySlot;
	final int vectorSize;

	public static Vector getAnyFeatureVector(int size) {
		double[] features = new double[size];
		Arrays.fill(features, ANY);
		return Vectors.dense(features);
	}

	/**
	 * The synchronised map containing the user preference tensor. The total
	 * watch time of a specific tensor is held in the value of the map.
	 */
	Map<UserPreference, UserPreferenceTensor> syncMap;

	/**
	 * Default constructor that build an empty synchronized HashMap of
	 * UserPreferenceTensor.
	 */
	public UserPreferenceTensorCollection(boolean anyUser, boolean anyProgram, int vectorSize, boolean anySlot) {
		this.anyUser = anyUser;
		this.anyProgram = anyProgram;
		this.vectorSize = vectorSize;
		this.anySlot = anySlot;
		syncMap = Collections
				.synchronizedMap(new HashMap<UserPreference, UserPreferenceTensor>());
	}
	
	public boolean anyUser(){
		return this.anyUser;
	}
	
	public boolean anyProgram(){
		return this.anyProgram;
	}
	
	public int vectorSize(){
		return this.vectorSize;
	}
	
	public boolean anySlot(){
		return this.anySlot;
	}

	/**
	 * Copy constructor that make a deep copy of the given user preference
	 * tensor collection.
	 * 
	 * @param c
	 *            The user preference tensor collection to copy.
	 */
	public UserPreferenceTensorCollection(UserPreferenceTensorCollection c) {
		syncMap = Collections.synchronizedMap(c.getSyncMap());
		this.anyUser = c.anyUser();
		this.anyProgram = c.anyProgram();
		this.vectorSize = c.vectorSize();
		this.anySlot = c.anySlot();
	}

	public List<UserPreferenceTensor> getAllUserPreferenceTensors() {
		return syncMap.entrySet().stream().map(Entry::getValue)
				.collect(Collectors.toList());
	}

	/**
	 * Method that returns all the user preference tensors with the
	 * corresponding user id, program feature vectors and slot. It is also
	 * possible to give the UserPreferenceTensorCollection.ANY value to any of
	 * the arguments, including every value in the program feature vector. For
	 * example if the ANY value is given for user id, the returned list will
	 * contains tensors corresponding to the given slot and program feature
	 * vector for any users.
	 * 
	 * @param userId
	 *            The wanted user id.
	 * @param programFeatures
	 *            The wanted program feature vector.
	 * @param slot
	 *            The wanted slot.
	 * @return A list containing all the user preference tensors corresponding
	 *         to the given attributes.
	 */
	public UserPreferenceTensor getUserPreferenceTensor(UserPreference userPreference) {
		return syncMap.getOrDefault(userPreference, new UserPreferenceTensor(userPreference));
	}

	/**
	 * 
	 * @param userPreference
	 * @return
	 */
	public int getUserPreferenceTensorWatchTime(UserPreference userPreference) {
		return getUserPreferenceTensor(userPreference).totalWatchTime();
	}

	/**
	 * Method to add the watching time of this tensor to the corresponding
	 * tensor in the map. If it does not exist already in the map it is simply
	 * added to it.
	 * 
	 * @param tensor
	 *            The tensor to add to the collection.
	 */
	public void add(UserPreferenceTensor tensor) {
		int userId = anyUser ? ANY : tensor.userId();
		Vector programFeatureVector = anyProgram ? getAnyFeatureVector(vectorSize()) : tensor.programFeatureVector();
		short slot = anySlot ? ANY : tensor.slot();
		UserPreference actualPreference = new UserPreference(userId, programFeatureVector, slot);
		UserPreferenceTensor actualTensor = new UserPreferenceTensor(actualPreference);
		actualTensor.incrementValue(tensor.totalWatchTime());
		if (syncMap.containsKey(actualPreference)) {
			syncMap.get(actualPreference).incrementValue(actualTensor.totalWatchTime());
		} else {
			syncMap.put(actualPreference, actualTensor);
		}
	}

	/**
	 * Method that add a whole collection of user preference tensors. It simply
	 * call the add method for all the tensors in the given collection.
	 * 
	 * @param collection
	 *            The user preference tensors to add to this collection.
	 */
	public void addAll(UserPreferenceTensorCollection collection) {
		for (Entry<UserPreference, UserPreferenceTensor> entry : collection
				.getSyncMap().entrySet()) {
			add(entry.getValue());
		}
	}

	/**
	 * Method that checks if the collection is emtpy or not.
	 * 
	 * @return True if the collection is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return syncMap.isEmpty();
	}

	private Map<UserPreference, UserPreferenceTensor> getSyncMap() {
		return syncMap;
	}

}
