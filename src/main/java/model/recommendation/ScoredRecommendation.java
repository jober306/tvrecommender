package model.recommendation;

import data.TVProgram;


public class ScoredRecommendation extends Recommendation{
	
	double score;
	
	public ScoredRecommendation(TVProgram tvProgram, double score) {
		super(tvProgram);
		this.score = score;
	}
	
	public double score(){
		return score;
	}
	
	@Override
	public String toString() {
		return "(" + super.toString() + ", " + score + ")";
	}
}
