package model.tensor;

import java.io.Serializable;

import org.apache.spark.util.AccumulatorV2;

/**
 * Accumulator class for spark. It is used to accumulates user preference tensors.
 * To use it simply use the default constructor and use the spark context to register it.
 * @author Jonathan Bergeron
 *
 */
public class UserPreferenceTensorCollectionAccumulator extends AccumulatorV2<UserPreferenceTensor, UserPreferenceTensorCollection> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	UserPreferenceTensorCollection collection;
	final boolean anyUser;
	final boolean anySlot;
	final int vectorSize;
	final boolean anyProgram;
	
	/**
	 * Default constructor of this class. It creates an empty collection.
	 */
	public UserPreferenceTensorCollectionAccumulator(boolean anyUser, boolean anyProgram, int vectorSize, boolean anySlot){
		collection = new UserPreferenceTensorCollection(anyUser, anyProgram, vectorSize, anySlot);
		this.anyUser = anyUser;
		this.anyProgram = anyProgram;
		this.vectorSize = vectorSize;
		this.anySlot = anySlot;
	}
	
	/**
	 * Copy constructor of this class. It makes a deep copy of the tensors in the given collection.
	 * @param collection The user preference tensor collection to copy.
	 */
	public UserPreferenceTensorCollectionAccumulator(UserPreferenceTensorCollection collection){
		this.collection = new UserPreferenceTensorCollection(collection);
		this.anyUser = collection.anyUser();
		this.anyProgram = collection.anyProgram();
		this.vectorSize = collection.vectorSize();
		this.anySlot = collection.anySlot();
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
		collection = new UserPreferenceTensorCollection(collection.anyUser(), collection.anyProgram(), collection.vectorSize(), collection.anySlot());
	}

	@Override
	public UserPreferenceTensorCollection value() {
		return collection;
	}

}
