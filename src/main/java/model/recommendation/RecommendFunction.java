package model.recommendation;

import java.util.List;

import model.data.TVProgram;
import model.data.User;

@FunctionalInterface
public interface RecommendFunction<U extends User, P extends TVProgram, R extends Recommendation> {
	Recommendations<U, R> recommend(U user, List<? extends P> tvPrograms);
}
