package model;

import java.util.List;

import data.TVProgram;
import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendations;

@FunctionalInterface
public interface RecommendFunction<T extends TVProgram, R extends AbstractRecommendation> {
	Recommendations<R> recommend(int userId, List<T> tvPrograms);
}
