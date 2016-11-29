package mllib.model.tensor;

import java.util.HashMap;

/**
 * Class that represents a collection of user preference tensor that extends hash map. The main advantage is when using
 * the put method the time value of the new user preference tensor will be added if one was already existing with same
 * user id, program feature vector and slot. It also offers utilities method.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollection extends HashMap<UserPreferenceTensor,UserPreferenceTensor>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor that build an empty HashMap of UserPreferenceTensor.
	 */
	public UserPreferenceTensorCollection(){
		super();
	}
	
	@Override
	public UserPreferenceTensor put(UserPreferenceTensor key, UserPreferenceTensor value){
		if(containsKey(key)){
			UserPreferenceTensor current  = get(key);
			UserPreferenceTensor old = new UserPreferenceTensor(current.getUserId(), current.getProgramFeatureVector(), current.getSlot());
			current.IncrementValue(value.getTotalWatchTime());
			return old;
		}else{
			put(key, value);
			return value;
		}
	}

}
