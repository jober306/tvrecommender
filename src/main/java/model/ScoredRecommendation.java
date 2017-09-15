package model;

import data.TVProgram;


public class ScoredRecommendation extends Recommendation{
	
	double score;
	
	public ScoredRecommendation(TVProgram program, double score) {
		super(program);
		this.score = score;
	}
	
	public double getScore(){
		return score;
	}
}
