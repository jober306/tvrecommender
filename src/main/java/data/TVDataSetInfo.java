package data;

import java.io.Serializable;

import model.information.AbstractInformation;

public class TVDataSetInfo extends AbstractInformation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	final String name;
	final int numberOfUsers;
	final int numberOfTvPrograms;
	final long numberOfTVEvents;
	
	public TVDataSetInfo(String name, int numberOfUsers, int numberOfTVPrograms, long numberOfTVEvents){
		this.name = name;
		this.numberOfUsers = numberOfUsers;
		this.numberOfTvPrograms = numberOfTVPrograms;
		this.numberOfTVEvents = numberOfTVEvents;
	}

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + name + "\n");
		sb.append("Number of users: " + numberOfUsers + "\n");
		sb.append("Number of tv programs: " + numberOfTvPrograms + "\n");
		sb.append("Number of tv events: " + numberOfTVEvents + "\n");
		return sb.toString();
	}
}
