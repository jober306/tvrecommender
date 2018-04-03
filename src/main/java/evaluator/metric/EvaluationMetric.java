package evaluator.metric;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import data.EvaluationContext;
import evaluator.result.MetricResults;
import model.recommendation.AbstractRecommendation;
import model.recommendation.Recommendations;
import util.StreamUtilities;

/**
 * Interface class that represent an evaluation metric. Class implementing it must be able to evaluate
 * recommendations given the ground truth.
 * @author Jonathan Bergeron
 *
 * @param <R> The type of recommendation.
 */
public interface EvaluationMetric<R extends AbstractRecommendation> {
	
	public String name();
	
	/**
	 * Method that evaluates a stream of recommendations given its evaluation context.
	 * @param recommendationsStream The recommendations stream made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return A stream containing the evaluation results. The results are ordered in the same way the recommendations are.
	 */
	default public MetricResults evaluate(Stream<Recommendations<R>> recommendationsStream, EvaluationContext<?, ?> context){
		Map<Integer, Double> userScores = StreamUtilities.toMapAverage(recommendationsStream, Recommendations::userId, recommendations -> evaluate(recommendations, context));
		return new MetricResults(name(), userScores);
	}
	
	/**
	 * Method that evaluates recommendations given its evaluation context.
	 * @param recommendations The recommendations made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return The evaluation result
	 */
	default public double evaluate(Recommendations<R> recommendations, EvaluationContext<?,?> context) {
		List<Integer> groundTruth = context.getGroundTruth().get(recommendations.userId());
		return evaluate(recommendations, groundTruth);
	}
	
	/**
	 * Method that evaluates recommendations given the ground truth.
	 * @param recommendations The recommendations made by a recommender.
	 * @param groundTruth The list of program id actually relevant.
	 * @return The evaluation result
	 */
	public double evaluate(Recommendations<R> recommendations, List<Integer> groundTruth);
}
