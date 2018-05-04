package model.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	final int hashCode;
	
	final int id;
	
	public User(int id){
		this.id = id;
		this.hashCode = new HashCodeBuilder(17, 37).append(id()).toHashCode();
	}
	
	public int id(){
		return this.id;
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
		return this.hashCode;
	}
}
