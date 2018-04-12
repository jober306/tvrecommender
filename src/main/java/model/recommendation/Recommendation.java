package model.recommendation;

import data.TVProgram;

public class Recommendation {
	
	final TVProgram tvProgram;
	
	public Recommendation(TVProgram tvProgram) {
		this.tvProgram = tvProgram;
	}
	
	public TVProgram tvProgram(){
		return tvProgram;
	}
}
