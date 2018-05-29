package evaluator.metric;

import java.util.Map;
import java.util.stream.Stream;

import data.EvaluationContext;
import evaluator.result.MetricResults;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;
import util.collections.StreamUtilities;

/**
 * Interface class that represent an evaluation metric. Class implementing it must be able to evaluate
 * recommendations given the ground truth.
 * @author Jonathan Bergeron
 *
 * @param <R> The type of recommendation.
 */
public interface EvaluationMetric<U extends User, P extends TVProgram> {
	
	public String name();
	
	/**
	 * Method that evaluates a stream of recommendations given its evaluation context.
	 * @param recommendationsStream The recommendations stream made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return A stream containing the evaluation results. The results are ordered in the same way the recommendations are.
	 */
	default public MetricResults<U> evaluate(Stream<Recommendations<U, P>> recommendationsStream, EvaluationContext<U, P, ?> context){
		Map<U, Double> userScores = StreamUtilities.toMapAverage(recommendationsStream, Recommendations::user, recommendation -> evaluate(recommendation, context));
		return new MetricResults<>(name(), userScores);
	}
	
	/**
	 * Method that evaluates recommendations given its evaluation context.
	 * @param recommendations The recommendations made by a recommender.
	 * @param context The evaluation context in which the recommendations have been made.
	 * @return The evaluation result
	 */
	public double evaluate(Recommendations<U, P> recommendations, EvaluationContext<U, P,?> evaluationContext);
}
