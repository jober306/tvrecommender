package recommender;

import java.time.LocalDateTime;
import java.util.List;

import data.Context;
import data.EvaluationContext;
import data.TVEvent;
import data.TVProgram;

public abstract class TVRecommender<T extends TVProgram, U extends TVEvent> {

	/**
	 * The context of this recommender;
	 */
	final protected Context<T,U> context;
	
	final RecommendFunction<T> recommendFunctionRef;

	public TVRecommender(Context<T, U> context) {
		this.context = context;
		if(context instanceof EvaluationContext<?, ?>){
			recommendFunctionRef = this::recommendForTesting;
		}else{
			recommendFunctionRef = this::recommendNormally;
		}
	}
	
	public Context<T, U> getContext(){
		return this.context;
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
	public List<Integer> recommend(int userId,
			LocalDateTime targetWatchTime, int numberOfResults){
		List<T> tvPrograms = context.getEPG().getListProgramsAtWatchTime(targetWatchTime);
		return recommend(userId, numberOfResults, tvPrograms);
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
	public List<Integer> recommend(int userId,
			LocalDateTime startTargetTime, LocalDateTime endTargetTime,
			int numberOfResults){
		List<T> tvPrograms = context.getEPG().getListProgramsBetweenTimes(startTargetTime, endTargetTime);
		return recommend(userId, numberOfResults, tvPrograms);
	}
	
	public List<Integer> recommend(int userId, int numberOfResults, List<T> tvProrams){
		return recommendFunctionRef.recommend(userId, numberOfResults, tvProrams);
	}
	
	abstract protected List<Integer> recommendNormally(int userId, int numberOfResults, List<T> tvPrograms);
	abstract protected List<Integer> recommendForTesting(int userId, int numberOfResults, List<T> tvPrograms);
}
