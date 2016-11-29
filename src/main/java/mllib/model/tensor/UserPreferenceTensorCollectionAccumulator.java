package mllib.model.tensor;

import org.apache.spark.util.AccumulatorV2;

public class UserPreferenceTensorCollectionAccumulator extends AccumulatorV2<UserPreferenceTensor, UserPreferenceTensorCollection> {

	private static final long serialVersionUID = 1L;
	
	UserPreferenceTensorCollection collection;
	
	public UserPreferenceTensorCollectionAccumulator(){
		collection = new UserPreferenceTensorCollection();
	}
	
	public UserPreferenceTensorCollectionAccumulator(UserPreferenceTensorCollection collection){
		this.collection = new UserPreferenceTensorCollection(collection); 
	}
	
	@Override
	public void add(UserPreferenceTensor arg0) {
		collection.put(arg0, arg0);
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
		collection.putAll(arg0.value());
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
