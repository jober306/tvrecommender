package recommender;

import java.util.List;

import data.TVProgram;

@FunctionalInterface
public interface RecommendFunction<T extends TVProgram> {
	List<Integer> recommend(int userId, int numberOfResults, List<T> tvProrams);
}
