package evaluator.metric;

import java.util.Set;

import model.data.TVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * An optimistic metric for those going through rough times that always return a score of 1!
 * (Actually used to test the AbstractEvaluationMetric)
 * @author Jonathan Bergeron
 *
 * @param <R> The type of the recommendation.
 */
public class OneMetric implements EvaluationMetric<Recommendation>{

	@Override
	public double evaluate(Recommendations<?, ? extends Recommendation> recommendations, Set<? extends TVProgram> groundTruth) {
		return 1.0d;
	}

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
}
