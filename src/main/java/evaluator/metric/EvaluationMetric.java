package evaluator.metric;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import data.EvaluationContext;
import evaluator.result.EvaluationResult;
import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendations;

/**
 * Interface class that represent an evaluation metric. Class implementing it must be able to evaluate
 * recommendations given the ground truth.
 * @author Jonathan Bergeron
 *
 * @param <R> The type of recommendation.
 */
public interface EvaluationMetric<R extends AbstractRecommendation> {
	
	/**
	 * Method that evaluates recommendations given the ground truth.
	 * @param recommendations The recommendations made by a recommender.
	 * @param groundTruth The list of program id actually relevant.
	 * @return The evaluation result
	 */
	public EvaluationResult evaluate(Recommendations<R> recommendations, List<Integer> groundTruth);
	
	/**
	 * Method that returns the mean of all the evaluation it has made.
	 * @return The mean score of all the evaluations.
	 */
	public double mean();
	
	/**
	 * Method that returns the geometric mean of all the evaluation it has made.
	 * @return The geometric mean score of all the evaluations.
	 */
	public double geometricMean();
	
	/**
	 * Method that evaluates a stream of recommendations given its evaluation context.
	 * @param recommendationsStream The recommendations stream made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return A stream containing the evaluation results. The results are ordered in the same way the recommendations are.
	 */
	default public List<EvaluationResult> evaluate(Stream<Recommendations<R>> recommendationsStream, EvaluationContext<?, ?> context){
		return recommendationsStream
				.map(recommendations -> evaluate(recommendations, context))
				.collect(toList());
	}
	
	/**
	 * Method that evaluates recommendations given its evaluation context.
	 * @param recommendations The recommendations made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return The evaluation result
	 */
	default public EvaluationResult evaluate(Recommendations<R> recommendations, EvaluationContext<?,?> context) {
		List<Integer> groundTruth = context.getGroundTruth().get(recommendations.userId());
		return evaluate(recommendations, groundTruth);
	}
}
