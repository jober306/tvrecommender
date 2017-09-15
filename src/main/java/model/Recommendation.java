package model;

import data.TVProgram;

public class Recommendation {
	
	TVProgram program;
	
	public Recommendation(TVProgram program){
		this.program = program;
	}
	
	public TVProgram getProgram(){
		return program;
	}
}
