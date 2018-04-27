package model.matrix;

import java.io.Serializable;

import com.google.common.collect.BiMap;

import model.data.TVProgram;
import model.data.User;

public class UserTVProgramMapping<U extends User, T extends TVProgram> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	transient final BiMap<? extends U, Integer> userMapping;
	transient final BiMap<? extends T, Integer> tvProgramMapping;
	
	public UserTVProgramMapping(BiMap<? extends U, Integer> userMapping, BiMap<? extends T, Integer> tvProgramMapping){
		this.userMapping = userMapping;
		this.tvProgramMapping = tvProgramMapping;
	}
	
	public int userToIndex(User u){
		return userMapping.get(u);
	}
	
	public U indexToUser(int index){
		return userMapping.inverse().get(index);
	}
	
	public int tvProgramToIndex(TVProgram tvProgram){
		return tvProgramMapping.get(tvProgram);
	}
	
	public T indexToTVProgram(int index){
		return tvProgramMapping.inverse().get(index);
	}
}
