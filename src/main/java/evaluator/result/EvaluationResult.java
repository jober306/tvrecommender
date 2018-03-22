package evaluator.result;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class that encapsulates results of an evaluation made on a particular user.
 * It could be used to store more information as the training time and test time.
 * @author Jonathan Bergeron
 *
 */
public class EvaluationResult {
	
	/**
	 * The tested user id
	 */
	final int userId;
	
	/**
	 * The evaluation score obtained
	 */
	final double score;
	
	/**
	 * Constructor of the class
	 * @param userId The tested user id
	 * @param score The evaluation score obtained
	 */
	public EvaluationResult(int userId, double score) {
		this.userId = userId;
		this.score = score;
	}
	
	/**
	 * Method that returns the tested user id.
	 * @return The tested user id.
	 */
	public int userId() {
		return this.userId;
	}
	
	/**
	 * Method that return the evaluation score.
	 * @return The evaluation score.
	 */
	public double score() {
		return this.score;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EvaluationResult)) {
			return false;
		}
		EvaluationResult evaluationResult = (EvaluationResult) other;
		return new EqualsBuilder().append(userId, evaluationResult.userId())
				.append(score, evaluationResult.score())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(userId)
				.append(score)
				.toHashCode();
	}
}
