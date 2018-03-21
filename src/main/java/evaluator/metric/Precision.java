package evaluator.metric;

import java.util.List;

import data.TVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Class that calculates the precision evaluation metric. 
 * @author Jonathan Bergeron
 *
 */
public class Precision extends AbstractEvaluationMetric<Recommendation>{
	
	/**
	 * The number of recommendations that will be considered
	 */
	final int cutoff;
	
	/**
	 * Main constructor of this class.
	 * @param cutoff The number of recommendations that will be considered
	 */
	public Precision(int cutoff) {
		this.cutoff = cutoff;
	}
	
	@Override
	protected double performEvaluation(Recommendations<Recommendation> recommendations, List<Integer> groundTruth) {
		int truePositive = (int) recommendations.stream()
				.limit(cutoff)
				.map(Recommendation::tvProgram)
				.map(TVProgram::programId)
				.distinct()
				.filter(groundTruth::contains)
				.count();
			return (double) truePositive / cutoff;
	}

}
