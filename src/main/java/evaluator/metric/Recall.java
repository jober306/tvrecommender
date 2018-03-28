package evaluator.metric;

import java.util.List;
import java.util.stream.Collectors;

import data.TVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Class that calculates the recall evaluation metric.
 * @author Jonathan Bergeron
 *
 */
public class Recall extends AbstractEvaluationMetric<Recommendation>{
	

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
	protected double performEvaluation(Recommendations<Recommendation> recommendations,
			List<Integer> groundTruth) {
		List<Integer> distinctGroundTruth = groundTruth.stream()
				.distinct()
				.collect(Collectors.toList());
		double truePositive = (double) recommendations.stream()
			.limit(cutoff)
			.map(Recommendation::tvProgram)
			.map(TVProgram::programId)
			.distinct()
			.filter(distinctGroundTruth::contains)
			.count();
		return distinctGroundTruth.size() == 0 ? 0.0d : (double) truePositive / distinctGroundTruth.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("@");
		sb.append(cutoff);
		return sb.toString();
	}
}
