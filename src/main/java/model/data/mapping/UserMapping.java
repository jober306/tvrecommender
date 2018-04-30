package model.data.mapping;

import java.util.Set;

import model.data.User;
import util.function.SerializableFunction;

public class UserMapping<U extends User, UM> extends AbstractMapping<U, UM> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserMapping(Set<U> allValues, SerializableFunction<? super U, ? extends UM> valueMapper) {
		super(allValues, valueMapper);
	}
}
