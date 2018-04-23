package model.recommendation;

import data.TVProgram;

/**
 * Class that represents a recommendation with a score made by a recommender.
 * @author Jonathan Bergeron
 *
 */
public class ScoredRecommendation extends Recommendation{
	
	/**
	 * The score associated with this recommendations
	 */
	double score;
	
	/**
	 * Constructor of the class
	 * @param tvProgram The recommended tv program.
	 * @param score The score associated with this tv program.
	 */
	public ScoredRecommendation(TVProgram tvProgram, double score) {
		super(tvProgram);
		this.score = score;
	}
	
	/**
	 * Method that return the score of the recommendation.
	 * @return The score.
	 */
	public double score(){
		return score;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\nScore: " + score + "\n";
	}
}
