package model.data.mapping;

import java.io.Serializable;

public interface Mapping<V, VM> extends Serializable{
	
	public int valueToIndex(V value);
	
	public VM indexToMappedValue(int index);
	
	public int size();
}
