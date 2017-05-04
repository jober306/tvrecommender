package evaluator;

/**
 * Enum that represents different evaluation measure to pass to a recommender
 * evaluator.
 * 
 * @author Jonathan Bergeron
 *
 */
public enum EvaluationMeasure {
	MEAN_AVERAGE_PRECISION_AT_10, MEAN_AVERAGE_PRECISION_AT_20, MEAN_AVERAGE_PRECISION_AT_50, MEAN_AVERAGE_RECALL_AT_10, MEAN_AVERAGE_RECALL_AT_20, MEAN_AVERAGE_RECALL_AT_50
}
