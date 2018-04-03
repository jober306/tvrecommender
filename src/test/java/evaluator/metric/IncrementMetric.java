package evaluator.metric;

import java.util.List;

import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendations;

/**
 * A metric that increments the score it gives by one each time it evaluates recommendations.
 * Used to test the mean and geometric mean method.
 * @author Jonathan Bergeron
 *
 * @param <R> The type of the recommendation.
 */
public class IncrementMetric<R extends AbstractRecommendation> implements EvaluationMetric<R> {
	
	int currentValue = 1;
	
	@Override
	public double evaluate(Recommendations<R> recommendations, List<Integer> groundTruth) {
		return currentValue++;
	}
	
	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
