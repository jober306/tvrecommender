package model;

import java.util.List;

import data.TVProgram;

@FunctionalInterface
public interface RecommendFunction<T extends TVProgram> {
	List<? extends IRecommendation> recommend(int userId, int numberOfResults, List<T> tvPrograms);
}
