package model.recommendation;

import model.data.TVProgram;

/**
 * Class that represents a recommendation made by a recommender.
 * @author Jonathan Bergeron
 *
 */
public class Recommendation {
	
	/**
	 * The recommended TVProgram
	 */
	final TVProgram tvProgram;
	
	/**
	 * Main constructor of the class.
	 * @param tvProgram The recommended tv program.
	 */
	public Recommendation(TVProgram tvProgram) {
		this.tvProgram = tvProgram;
	}
	
	/**
	 * Get the recommended tv program.
	 * @return The recommended tv program.
	 */
	public TVProgram tvProgram(){
		return tvProgram;
	}
	
	@Override
	public String toString() {
		return tvProgram.toString();
	}
}
