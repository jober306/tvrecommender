package evaluator.result;

import java.util.Map;

import evaluator.metric.EvaluationMetric;

/**
 * Class that encapsulates results of an evaluation for a particular metric.
 * @author Jonathan Bergeron
 *
 */
public class EvaluationResults {
	
	/**
	 * The tested user id
	 */
	final Map<EvaluationMetric<?>, MetricResults> usersScorePerMetric;
	
	/**
	 * Constructor of the class
	 * @param userId The tested user id
	 * @param score The evaluation score obtained
	 */
	public EvaluationResults(Map<EvaluationMetric<?>, MetricResults> usersScorePerMetric) {
		this.usersScorePerMetric = usersScorePerMetric;
	}
	
	/**
	 * Method that returns the tested user id.
	 * @return The tested user id.
	 */
	public Map<EvaluationMetric<?>, MetricResults> usersScorePerMetric() {
		return this.usersScorePerMetric;
	}
}
