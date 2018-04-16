package evaluator.metric;

import java.util.List;

import data.TVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Class that calculates the recall evaluation metric.
 * @author Jonathan Bergeron
 *
 */
public class Recall implements EvaluationMetric<Recommendation>{
	

	/**
	 * The number of recommendations that will be considered
	 */
	final int cutoff;
	
	/**
	 * Main constructor of this class.
	 * @param cutoff The number of recommendations that will be considered
	 */
	public Recall(int cutoff) {
		this.cutoff = cutoff;
	}

	@Override
	public double evaluate(Recommendations<? extends Recommendation> recommendations,
			List<? extends TVProgram> groundTruth) {
		double truePositive = (double) recommendations.stream()
			.limit(cutoff)
			.map(Recommendation::tvProgram)
			.filter(groundTruth::contains)
			.count();
		return groundTruth.size() == 0 ? 0.0d : (double) truePositive / groundTruth.size();
	}
	
	@Override
	public String name() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("@");
		sb.append(cutoff);
		return sb.toString();
	}
}
