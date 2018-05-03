package evaluator.metric;

import java.util.Set;

import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

/**
 * An optimistic metric for those going through rough times that always return a score of 1!
 * (Actually used to test the AbstractEvaluationMetric)
 * @author Jonathan Bergeron
 *
 * @param <R> The type of the recommendation.
 */
public class OneMetric<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{

	@Override
	public double evaluate(Recommendations<U, P> recommendations, Set<P> groundTruth) {
		return 1.0d;
	}

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
