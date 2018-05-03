package model.recommendation;

import java.util.List;

import model.data.TVProgram;
import model.data.User;

@FunctionalInterface
public interface RecommendFunction<U extends User, P extends TVProgram> {
	Recommendations<U, P> recommend(U user, List<P> tvPrograms);
}
