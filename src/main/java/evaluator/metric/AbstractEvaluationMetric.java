package evaluator.metric;

import java.util.ArrayList;
import java.util.List;

import evaluator.result.EvaluationResult;
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
	
	/**
	 * The evaluation results accumulated so far by this metric.
	 * When an evaluation is made on some recommendations, the result will be added automatically to this list. 
	 */
	protected List<EvaluationResult> results;
	
	/**
	 * No arguments constructor that initiates an empty list of results.
	 */
	public AbstractEvaluationMetric() {
		results = new ArrayList<EvaluationResult>();
	}
	
	/**
	 * Method that return the list of evaluation results.
	 * @return The list of evaluation results.
	 */
	public final List<EvaluationResult> results(){
		return results;
	}
	
	@Override
	public final EvaluationResult evaluate(Recommendations<R> recommendations, List<Integer> groundTruth) {
		double score = performEvaluation(recommendations, groundTruth);
		EvaluationResult result = new EvaluationResult(recommendations.userId(), score);
		results.add(result);
		return result;
	}

	@Override
	public final double mean() {
		return results.stream()
				.mapToDouble(EvaluationResult::score)
				.average().orElse(0.0d);
	}

	
	public final double geometricMeans() {
		double product = results.stream()
				.mapToDouble(EvaluationResult::score)
				.reduce(1.0d, (a, b) -> a * b);
		double nthRoot = 1.0d / results.size();
		return Math.pow(product, nthRoot);
	}
}
