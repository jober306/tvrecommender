package evaluator.metric;

import java.util.List;

import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendations;

/**
 * An optimistic metric for those going through rough times that always return a score of 1!
 * (Actually used to test the AbstractEvaluationMetric)
 * @author Jonathan Bergeron
 *
 * @param <R> The type of the recommendation.
 */
public class OneMetric<R extends AbstractRecommendation> extends AbstractEvaluationMetric<R>{

	@Override
	protected double performEvaluation(Recommendations<R> recommendations, List<Integer> groundTruth) {
		return 1.0d;
	}

}
