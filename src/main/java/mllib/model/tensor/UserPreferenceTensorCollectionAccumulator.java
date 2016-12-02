package mllib.model.tensor;

import org.apache.spark.util.AccumulatorV2;

/**
 * Accumulator class for spark. It is used to accumulates user preference tensors.
 * To use it simply use the default constructor and use the spark context to register it.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollectionAccumulator extends AccumulatorV2<UserPreferenceTensor, UserPreferenceTensorCollection> {

	private static final long serialVersionUID = 1L;
	
	UserPreferenceTensorCollection collection;
	
	/**
	 * Default constructor of this class. It creates an empty collection.
	 */
	public UserPreferenceTensorCollectionAccumulator(){
		collection = new UserPreferenceTensorCollection();
	}
	
	/**
	 * Copy constructor of this class. It makes a deep copy of the tensors in the given collection.
	 * @param collection The user preference tensor collection to copy.
	 */
	public UserPreferenceTensorCollectionAccumulator(UserPreferenceTensorCollection collection){
		this.collection = new UserPreferenceTensorCollection(collection); 
	}
	
	@Override
	public void add(UserPreferenceTensor arg0) {
		collection.add(arg0);
	}

	@Override
	public AccumulatorV2<UserPreferenceTensor, UserPreferenceTensorCollection> copy() {
		return new UserPreferenceTensorCollectionAccumulator(collection);
	}

	@Override
	public boolean isZero() {
		return collection.isEmpty();
	}

	@Override
	public void merge(AccumulatorV2<UserPreferenceTensor, UserPreferenceTensorCollection> arg0) {
		collection.addAll(arg0.value());
	}

	@Override
	public void reset() {
		collection = new UserPreferenceTensorCollection();
	}

	@Override
	public UserPreferenceTensorCollection value() {
		return collection;
	}

}
