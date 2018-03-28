package evaluator.metric;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import data.EvaluationContext;
import evaluator.result.MetricResults;
import evaluator.result.SingleUserResult;
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
	 * Method that evaluates a stream of recommendations given its evaluation context.
	 * @param recommendationsStream The recommendations stream made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return A stream containing the evaluation results. The results are ordered in the same way the recommendations are.
	 */
	default public MetricResults evaluate(Stream<Recommendations<R>> recommendationsStream, EvaluationContext<?, ?> context){
		return new MetricResults(recommendationsStream
				.map(recommendations -> evaluate(recommendations, context))
				.collect(toList()));
	}
	
	/**
	 * Method that evaluates recommendations given its evaluation context.
	 * @param recommendations The recommendations made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return The evaluation result
	 */
	default public SingleUserResult evaluate(Recommendations<R> recommendations, EvaluationContext<?,?> context) {
		List<Integer> groundTruth = context.getGroundTruth().get(recommendations.userId());
		return evaluate(recommendations, groundTruth);
	}
	
	/**
	 * Method that evaluates recommendations given the ground truth.
	 * @param recommendations The recommendations made by a recommender.
	 * @param groundTruth The list of program id actually relevant.
	 * @return The evaluation result
	 */
	public SingleUserResult evaluate(Recommendations<R> recommendations, List<Integer> groundTruth);
}
