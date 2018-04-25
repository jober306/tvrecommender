package model.recommendation;

import java.util.List;

import model.data.TVProgram;

@FunctionalInterface
public interface RecommendFunction<T extends TVProgram, R extends Recommendation> {
	Recommendations<R> recommend(int userId, List<T> tvPrograms);
}
