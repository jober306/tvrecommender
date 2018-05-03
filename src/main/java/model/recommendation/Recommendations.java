package model.recommendation;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import model.data.TVProgram;
import model.data.User;

/**
 * Class that represents a list of recommendations made for a particular user.
 * @author Jonathan Bergeron
 *
 * @param <T> The type of recommendations
 */
public class Recommendations<U extends User, P extends TVProgram> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The user id for whom recommendations were made.
	 */
	final U user;
	
	/**
	 * The sorted list of recommendations, from most relevant to less relevant.
	 */
	final List<P> recommendations;
	
	/**
	 * Main constructor of the class.
	 * @param user The user for whom recommendations were made.
	 * @param recommendations The sorted list of recommendations made for the user. It should be sorted from most
	 * relevant to less relevant.
	 */
	public Recommendations(U user, List<P> recommendations) {
		this.user = user;
		this.recommendations = recommendations;
	}
	
	public U user(){
		return this.user;
	}
	
	public int userId(){
		return this.user.id();
	}
	
	/**
	 * Method that returns the recommendations at given index.
	 * @param index The index.
	 * @return The index'th recommendation.
	 */
	public P get(int index){
		return recommendations.get(index);
	}
	
	/**
	 * Method that returns the stream of recommendations.
	 * @return The stream of recommendations.
	 */
	public Stream<P> stream(){
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
