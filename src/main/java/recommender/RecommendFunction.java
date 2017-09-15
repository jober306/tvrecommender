package recommender;

import java.util.List;

import model.Recommendation;
import data.TVProgram;

@FunctionalInterface
public interface RecommendFunction<T extends TVProgram> {
	List<? extends Recommendation> recommend(int userId, int numberOfResults, List<T> tvProrams);
}
