package evaluator.metric;

import java.util.Set;

import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

/**
 * A metric that increments the score it gives by one each time it evaluates recommendations.
 * Used to test the mean and geometric mean method.
 * @author Jonathan Bergeron
 *
 * @param <R> The type of the recommendation.
 */
public class IncrementMetric<U extends User, P extends TVProgram> implements EvaluationMetric<U, P> {
	
	int currentValue = 1;
	
	@Override
	public double evaluate(Recommendations<U, P> recommendations, Set<P> groundTruth) {
		return currentValue++;
	}
	
	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
