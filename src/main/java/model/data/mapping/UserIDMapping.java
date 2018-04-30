package model.data.mapping;

import java.util.Set;

import model.data.User;

public class UserIDMapping<U extends User> extends UserMapping<U, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserIDMapping(Set<U> allValues) {
		super(allValues, User::id);
	}
	
}
