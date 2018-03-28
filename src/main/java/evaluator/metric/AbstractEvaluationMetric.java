package evaluator.metric;

import java.util.List;

import evaluator.result.SingleUserResult;
import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendations;

/**
 * An abstract class that holds basic functionalities of an evaluation metric.
 * It also holds all the evaluation results it had made, so it cannot be a singleton.
 * Extends this class to create a concrete implementation of an evaluation metric.
 * @author Jonathan Bergeron
 *
 * @param <R> The type of recommendation the evaluation metric deals with
 */
public abstract class AbstractEvaluationMetric<R extends AbstractRecommendation> implements EvaluationMetric<R>{
	
	/**
	 * Method that perform the evaluation process.
	 * @param recommendations The list of recommendations returned by a recommender.
	 * @param groundTruth The list of relevant tv show indexes.
	 * @return The evaluation score.f
	 */
	abstract protected double performEvaluation(Recommendations<R> recommendations, List<Integer> groundTruth);
	
	
	@Override
	public final SingleUserResult evaluate(Recommendations<R> recommendations, List<Integer> groundTruth) {
		double score = performEvaluation(recommendations, groundTruth);
		return new SingleUserResult(recommendations.userId(), score);
	}
}
