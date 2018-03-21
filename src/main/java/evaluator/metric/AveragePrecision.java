package evaluator.metric;

import static java.util.stream.Collectors.toList;

import java.util.List;

import data.TVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Class that calculates the average precision evaluation metric. 
 * @author Jonathan Bergeron
 *
 */
public class AveragePrecision extends AbstractEvaluationMetric<Recommendation>{
	
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
	public double performEvaluation(Recommendations<Recommendation> recommendations, List<Integer> groundTruth) {
		List<Integer> recommendedTVShowIndexes = recommendations.stream()
				.map(Recommendation::tvProgram)
				.map(TVProgram::programId)
				.collect(toList());
		return calculateAveragePrecision(cutoff, recommendedTVShowIndexes, groundTruth);
	}

	private double calculateAveragePrecision(int cutoff, List<Integer> recommendedTVShowIndexes, List<Integer> actuallySeenTVShowIndexes) {
		double averagePrecision = 0.0d;
		double truePositiveRecommendedTVShow = 0;
		for (int k = 1; k <= Math.min(recommendedTVShowIndexes.size(), cutoff); k++) {
			int recommendedTVShowIndex = recommendedTVShowIndexes.get(k - 1);
			if (actuallySeenTVShowIndexes.contains(recommendedTVShowIndex)) {
				truePositiveRecommendedTVShow++;
				averagePrecision += truePositiveRecommendedTVShow / k;
			}
		}
		return averagePrecision / actuallySeenTVShowIndexes.size();
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
