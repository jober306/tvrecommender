package model.data.mapping;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import scala.Tuple2;
import util.collections.StreamUtilities;
import util.function.SerializableFunction;

public class AbstractMapping<V, VM> implements Mapping<V, VM>, Serializable{

	private static final long serialVersionUID = 1L;
	
	final BiMap<VM, Integer> mapping;
	final SerializableFunction<? super V, ? extends VM> valueMapper;
	
	public AbstractMapping(Set<V> allValues, SerializableFunction<? super V, ? extends VM> valueMapper){
		this.valueMapper = valueMapper;
		this.mapping = createMapping(allValues);
	}
	
	private HashBiMap<VM, Integer> createMapping(Set<V> allValues){
		Stream<? extends VM> allMappedTVPrograms = allValues.stream().map(valueMapper).distinct();
		return HashBiMap.create(StreamUtilities.zipWithIndex(allMappedTVPrograms).collect(Collectors.toMap(Tuple2::_1, Tuple2::_2)));
	}
	
	@Override
	public int valueToIndex(V value) {
		return mapping.get(valueMapper.apply(value));
	}

	@Override
	public VM indexToMappedValue(int index) {
		return mapping.inverse().get(index);
	}
	
	@Override
	public int size(){
		return mapping.size();
	}
}
