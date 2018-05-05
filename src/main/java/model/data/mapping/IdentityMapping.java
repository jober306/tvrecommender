package model.data.mapping;

import java.util.Set;

import util.function.SerializableFunction;

public class IdentityMapping<V> extends AbstractMapping<V, V>{

	private static final long serialVersionUID = 1L;

	public IdentityMapping(Set<V> allValues) {
		super(allValues, (SerializableFunction<? super V, ? extends V>) v -> v);
	}

}
