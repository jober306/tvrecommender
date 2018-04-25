package recommender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Context;
import data.EvaluationContext;
import model.data.TVEvent;
import model.data.TVProgram;
import model.information.Informative;
import model.recommendation.RecommendFunction;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

public abstract class TVRecommender<T extends TVProgram, U extends TVEvent<T>, R extends Recommendation> implements Informative{
	
	abstract protected Recommendations<R> recommendNormally(int userId, List<T> tvPrograms);

	abstract protected Recommendations<R> recommendForTesting(int userId, List<T> tvPrograms);
	
	abstract protected Map<String, String> additionalParameters();
	
	public Map<String, String> parameters(){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("Number of Recommendations", Integer.toString(numberOfRecommendations));
		parameters.putAll(additionalParameters());
		return parameters;
	}
	
	abstract public void train();
	
	/**
	 * The context of this recommender;
	 */
	protected Context<T, U> context;

	RecommendFunction<T, R> recommendFunctionRef;
	
	protected int numberOfRecommendations;
	
	public TVRecommender(int numberOfRecommendations){
		this.numberOfRecommendations = numberOfRecommendations;
	}

	public TVRecommender(Context<T, U> context, int numberOfRecommendations) {
		this.setContext(context);
		this.numberOfRecommendations = numberOfRecommendations;
	}
	
	public void setContext(Context<T, U> context){
		this.context = context;
		if (context instanceof EvaluationContext) {
			recommendFunctionRef = this::recommendForTesting;
		} else {
			recommendFunctionRef = this::recommendNormally;
		}
	}
	
	public void closeContextDatasets(){
		context.close();
	}
	
	public RecommenderInfo info(){
		return new RecommenderInfo(this.getClass().getSimpleName(), parameters());
	}
	
	public Context<T, U> getContext() {
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
	public Recommendations<R> recommend(int userId, LocalDateTime targetWatchTime) {
		List<T> tvPrograms = context.getEPG().getListProgramsAtWatchTime(
				targetWatchTime);
		return recommend(userId, tvPrograms);
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
	public Recommendations<R> recommend(int userId, LocalDateTime startTargetTime,
			LocalDateTime endTargetTime) {
		List<T> tvPrograms = context.getEPG().getListProgramsBetweenTimes(
				startTargetTime, endTargetTime);
		return recommend(userId, tvPrograms);
	}

	public Recommendations<R> recommend(int userId, List<T> tvProrams) {
		return recommendFunctionRef.recommend(userId, tvProrams);
	}
	
	
	
}
