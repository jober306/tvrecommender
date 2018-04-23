package model.recommendation;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class that represents a list of recommendations made for a particular user.
 * @author Jonathan Bergeron
 *
 * @param <T> The type of recommendations
 */
public class Recommendations<T extends Recommendation> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The user id for whom recommendations were made.
	 */
	final int userId;
	
	/**
	 * The sorted list of recommendations, from most relevant to less relevant.
	 */
	final List<T> recommendations;
	
	/**
	 * Main constructor of the class.
	 * @param userId The user id for whom recommendations were made.
	 * @param recommendations The sorted list of recommendations made for the user. It should be sorted from most
	 * relevant to less relevant.
	 */
	public Recommendations(int userId, List<T> recommendations) {
		this.userId = userId;
		this.recommendations = recommendations;
	}
	
	/**
	 * Method that return the user id for whom recommendations were made.
	 * @return The user id.
	 */
	public int userId() {
		return this.userId;
	}
	
	/**
	 * Method that returns the recommendations at given index.
	 * @param index The index.
	 * @return The index'th recommendation.
	 */
	public T get(int index){
		return recommendations.get(index);
	}
	
	/**
	 * Method that returns the stream of recommendations.
	 * @return The stream of recommendations.
	 */
	public Stream<T> stream(){
		return recommendations.stream();
	}
	
	/**
	 * Method that return the size of the recommendations list
	 * @return The size of the recommendations list.
	 */
	public int size(){
		return recommendations.size();
	}
}
