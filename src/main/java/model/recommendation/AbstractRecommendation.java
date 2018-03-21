package model.recommendation;

import data.TVProgram;

public abstract class AbstractRecommendation {
	
	final TVProgram tvProgram;
	
	public AbstractRecommendation(TVProgram tvProgram) {
		this.tvProgram = tvProgram;
	}
	
	public TVProgram tvProgram(){
		return tvProgram;
	}
}
