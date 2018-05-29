package evaluator.metric;

import data.EvaluationContext;
import data.GroundModel;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

/**
 * The Novelty metric class. It calculates the expected entropy given a recommendation list by using a GroundModel
 * on the training data set.
 * @author Jonathan Bergeron
 *
 * @param <U> The type of user
 * @param <P> The type of tv program
 */
public class Novelty<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{
	
	final double additiveSmoothing;
	
	/**
	 * Default constructor of this class. Sets the additive smoothing when
	 * computing probabilities to 1 by default.
	 */
	public Novelty() {
		additiveSmoothing = 1.0d;
	}
	
	/**
	 * Constructor of this class.
	 * @param additiveSmoothing The additive smoothing used when computing probabilities.
	 */
	public Novelty(double additiveSmoothing) {
		this.additiveSmoothing = additiveSmoothing;
	}
	
	@Override
	public String name() {
		return "Novelty";
	}

	@Override
	public double evaluate(Recommendations<U, P> recommendations, EvaluationContext<U, P,?> evaluationContext){	
		GroundModel<U, P> groundModel = new GroundModel<>(evaluationContext.getTrainingSet()); 
		final double log2 = Math.log(2);
		double sum = recommendations.stream()
				.mapToDouble(recommendation -> groundModel.probabilityTVProgramIsChosen(recommendation, 1.0d))
				.map(p -> Math.log(p) / log2)
				.sum();
		return -1.0d * sum / recommendations.size();
	}

}
