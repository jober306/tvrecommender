package evaluator.result;

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
}
