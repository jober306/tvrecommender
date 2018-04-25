package model.matrix;

import com.google.common.collect.BiMap;

import model.data.TVProgram;
import model.data.User;

public class UserTVProgramMapping<U extends User, T extends TVProgram> {

	final BiMap<? extends U, Long> userMapping;
	final BiMap<? extends TVProgram, Long> tvProgramMapping;
	
	public UserTVProgramMapping(BiMap<U, Long> userMapping, BiMap<T, Long> tvProgramMapping){
		this.userMapping = userMapping;
		this.tvProgramMapping = tvProgramMapping;
	}
	
	public long mapUser(User u){
		return userMapping.get(u);
	}
	
	public U mapToUser(long index){
		return userMapping.inverse().get(index);
	}
	
	public long mapTVProgram(T tvProgram){
		return tvProgramMapping.get(tvProgram);
	}
	
}
