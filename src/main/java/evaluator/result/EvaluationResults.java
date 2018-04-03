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
	final Map<String, MetricResults> usersScorePerMetric;
	final EvaluationInfo evaluationInfo;
	
	/**
	 * Constructor of the class
	 * @param userId The tested user id
	 * @param score The evaluation score obtained
	 */
	public EvaluationResults(Map<String, MetricResults> usersScorePerMetric, EvaluationInfo evaluationInfo) {
		this.usersScorePerMetric = usersScorePerMetric;
		this.evaluationInfo = evaluationInfo;
	}
	
	/**
	 * Method that returns the tested user id.
	 * @return The tested user id.
	 */
	public Map<String, MetricResults> usersScorePerMetric() {
		return this.usersScorePerMetric;
	}
}
