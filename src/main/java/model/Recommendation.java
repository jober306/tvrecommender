package model;

import data.TVProgram;

public class Recommendation implements IRecommendation{
	
	TVProgram tvProgram;
	
	public Recommendation(TVProgram tvProgram){
		this.tvProgram = tvProgram;
	}
	
	public TVProgram tvProgram(){
		return tvProgram;
	}
}
