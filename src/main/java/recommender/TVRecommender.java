package recommender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Context;
import data.EvaluationContext;
import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.information.Informative;
import model.recommendation.RecommendFunction;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * An abstract class representing a TV recommender. When extending this class, use wisely
 * the generic types definition. Follow those three simple rules:
 * 
 * 1. If the type doesn't matter, use the highest class in the hierarchy of this type. 
 *    For example, if the user class doesn't matter for the recommender use the 
 *    User class as type definition.
 *    
 * 2. If the type only depends on a child class, use this class to define this type. For example,
 *    my recommender depends on a user child class (let's say UserGender) that contains gender information.
 *    UserGender should be used as type definition.
 *    
 * 2. If the type can be anything, make your class generic for that type. 
 *    For example, if your recommender depends on feature extractors and feature extractor depend on type of TVProgram.
 *    Use generic public class MyRecommender<P extends TVProgram> extends TVRecommender<..., P, ..., ...>
 * 
 * @author Jonathan Bergeron
 *
 * @param <U> The type of user.
 * @param <P> The type of tv program.
 * @param <E> The type of tv event.
 * @param <R> The type of recommendations made by this recommender.
 */
public abstract class TVRecommender<U extends User, P extends TVProgram, E extends TVEvent<U, P>, R extends Recommendation> implements Informative{
	
	/**
	 * Method to recommend in a normal setting, i.e. when the instance of context is Context.
	 * @param userId The user for whom the recommendations are made
	 * @param tvPrograms The list of tv programs that can be recommended.
	 * @return
	 */
	abstract protected Recommendations<U, R> recommendNormally(U user, List<? extends P> tvPrograms);
	
	/**
	 * Method to recommend in a test setting, i.e. when the instance of context is EvaluationContext.
	 * @param userId The user for whom the recommendations are made
	 * @param tvPrograms The list of tv programs that can be recommended.
	 * @return
	 */
	abstract protected Recommendations<U, R> recommendForTesting(U user, List<? extends P> tvPrograms);
	
	/**
	 * 
	 * @return
	 */
	abstract protected Map<String, String> additionalParameters();
	
	/**
	 * 
	 */
	abstract public void train();
	
	/**
	 * The context of this recommender;
	 */
	protected Context<? extends U, ? extends P, ? extends E> context;

	RecommendFunction<U, P, R> recommendFunctionRef;
	
	protected int numberOfRecommendations;
	
	public TVRecommender(int numberOfRecommendations){
		this.numberOfRecommendations = numberOfRecommendations;
	}

	public TVRecommender(Context<? extends U, ? extends P, ? extends E> context, int numberOfRecommendations) {
		this.setContext(context);
		this.numberOfRecommendations = numberOfRecommendations;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> parameters(){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("Number of Recommendations", Integer.toString(numberOfRecommendations));
		parameters.putAll(additionalParameters());
		return parameters;
	}
	
	public void setContext(Context<? extends U, ? extends P, ? extends E> context){
		this.context = context;
		if (context instanceof EvaluationContext) {
			recommendFunctionRef = this::recommendForTesting;
		} else {
			recommendFunctionRef = this::recommendNormally;
		}
	}
	
	public RecommenderInfo info(){
		return new RecommenderInfo(this.getClass().getSimpleName(), parameters());
	}
	
	public Context<? extends U, ? extends P, ? extends E> getContext() {
		return this.context;
	}
	
	public int numberOfRecommendations() {
		return this.numberOfRecommendations;
	}
	
	public void setNumberOfRecommendations(int numberOfRecommendations) {
		this.numberOfRecommendations = numberOfRecommendations;
	}

	/**
	 * Method that returns the original (not the mapped one) tv show indexes in
	 * decreasing order of recommendation score.
	 * 
	 * @param userId
	 *            The user id to which the recommendation will be done.
	 * @param targetWatchTime
	 *            The ponctual time at which the recommendation should be done.
	 *            It means that only the programs occurring at this time will be
	 *            recommended.
	 * @param numberOfResults
	 *            The number of results that will be returned.
	 * @return The indexes in decreasing order from best of the best tv show.
	 */
	public Recommendations<U, R> recommend(U user, LocalDateTime targetWatchTime) {
		List<? extends P> tvPrograms = context.getEPG().getListProgramsAtWatchTime(
				targetWatchTime);
		return recommend(user, tvPrograms);
	}

	/**
	 * Method that returns the recommendations given a time window. 
	 * Target tv programs will be extracted from the epg and the recommendations
	 * will contains the program and depending on the recommender a recommendation score.
	 * The returned list is sorted from best recommendation to worst recommendation.
	 * 
	 * @param userId
	 *            The user id to which the recommendation will be done.
	 * @param startTargetTime
	 *            The start time on which recommendations will be made. Tv programs that started
	 *            before start target time but are ending after it will be included.
	 * @param endTargetTime
	 * 			  The end time on which recommendations will be made. Tv programs that end after this time
	 * 			  but started before it will be included.
	 * @param numberOfResults
	 *            The number of results that will be returned.
	 * @return The indexes in decreasing order from best of the best tv show.
	 */
	public Recommendations<U, R> recommend(U user, LocalDateTime startTargetTime,
			LocalDateTime endTargetTime) {
		List<? extends P> tvPrograms = context.getEPG().getListProgramsBetweenTimes(
				startTargetTime, endTargetTime);
		return recommend(user, tvPrograms);
	}

	public Recommendations<U, R> recommend(U user, List<? extends P> tvProrams) {
		return recommendFunctionRef.recommend(user, tvProrams);
	}
}
