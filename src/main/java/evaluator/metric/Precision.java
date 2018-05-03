package evaluator.metric;

import java.util.Set;

import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

/**
 * Class that calculates the precision evaluation metric. 
 * @author Jonathan Bergeron
 *
 */
public class Precision<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{
	
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
	public double evaluate(Recommendations<U, P> recommendations, Set<P> groundTruth){	
		int truePositive = (int) recommendations.stream()
				.limit(cutoff)
				.filter(groundTruth::contains)
				.count();
			return (double) truePositive / Math.min(cutoff, recommendations.size());
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
