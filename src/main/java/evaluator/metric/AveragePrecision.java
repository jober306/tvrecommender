package evaluator.metric;

import java.util.Set;

import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

/**
 * Class that calculates the average precision evaluation metric. 
 * @author Jonathan Bergeron
 *
 */
public class AveragePrecision<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{
	
	/**
	 * The number of recommendations that will be considered
	 */
	final int cutoff;
	
	/**
	 * Main constructor of this class.
	 * @param cutoff The number of recommendations that will be considered
	 */
	public AveragePrecision(int cutoff) {
		this.cutoff = cutoff;
	}
	
	@Override
	public String name() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("@");
		sb.append(cutoff);
		return sb.toString();
	}
	
	@Override
	public double evaluate(Recommendations<U, P> recommendations, Set<P> groundTruth){	
		double averagePrecision = 0.0d;
		double truePositiveRecommendedTVShow = 0;
		for (int k = 1; k <= Math.min(recommendations.size(), cutoff); k++) {
			TVProgram recommendation = recommendations.get(k-1);
			if (groundTruth.contains(recommendation)) {
				truePositiveRecommendedTVShow++;
				averagePrecision += truePositiveRecommendedTVShow / k;
			}
		}
		return groundTruth.size() == 0 ? 0.0d : averagePrecision / groundTruth.size();
	}
}
