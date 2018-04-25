package model.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	final int userId;
	
	public User(int userId){
		this.userId = userId;
	}
	
	public int id(){
		return this.userId;
	}
	
	@Override
	public boolean equals(Object obj) {		
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User user = (User) obj;
		return new EqualsBuilder().append(id(), user.id()).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id()).toHashCode();
	}
}
