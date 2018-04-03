package evaluator.metric;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;

import data.TVProgram;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Class that calculates the average precision evaluation metric. 
 * @author Jonathan Bergeron
 *
 */
public class AveragePrecision implements EvaluationMetric<Recommendation>{
	
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
	public double evaluate(Recommendations<Recommendation> recommendations, List<Integer> groundTruth) {
		List<Integer> recommendedTVShowIndexes = recommendations.stream()
				.map(Recommendation::tvProgram)
				.map(TVProgram::programId)
				.distinct()
				.collect(toList());
		List<Integer> distinctGroundTruth = groundTruth.stream()
				.distinct()
				.collect(Collectors.toList());
		return calculateAveragePrecision(cutoff, recommendedTVShowIndexes, distinctGroundTruth);
	}

	private double calculateAveragePrecision(int cutoff, List<Integer> recommendedTVShowIndexes, List<Integer> distinctGroundTruth) {
		double averagePrecision = 0.0d;
		double truePositiveRecommendedTVShow = 0;
		for (int k = 1; k <= Math.min(recommendedTVShowIndexes.size(), cutoff); k++) {
			int recommendedTVShowIndex = recommendedTVShowIndexes.get(k - 1);
			if (distinctGroundTruth.contains(recommendedTVShowIndex)) {
				truePositiveRecommendedTVShow++;
				averagePrecision += truePositiveRecommendedTVShow / k;
			}
		}
		return distinctGroundTruth.size() == 0 ? 0.0d : averagePrecision / distinctGroundTruth.size();
	}
}
