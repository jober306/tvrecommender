package model.data.mapping;

public interface Mapping<V, VM> {
	
	public int valueToIndex(V value);
	
	public VM indexToMappedValue(int index);
	
	public int size();
}
