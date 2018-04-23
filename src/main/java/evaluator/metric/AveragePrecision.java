package evaluator.metric;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;

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
	public double evaluate(Recommendations<? extends Recommendation> recommendations, Set<? extends TVProgram> groundTruth) {
		List<? extends TVProgram> recommendedTVShowIndexes = recommendations.stream()
				.map(Recommendation::tvProgram)
				.collect(toList());
		return calculateAveragePrecision(cutoff, recommendedTVShowIndexes, groundTruth);
	}

	private double calculateAveragePrecision(int cutoff, List<? extends TVProgram> recommendedTVShows, Set<? extends TVProgram> groundTruth) {
		double averagePrecision = 0.0d;
		double truePositiveRecommendedTVShow = 0;
		for (int k = 1; k <= Math.min(recommendedTVShows.size(), cutoff); k++) {
			TVProgram recommendation = recommendedTVShows.get(k-1);
			if (groundTruth.contains(recommendation)) {
				truePositiveRecommendedTVShow++;
				averagePrecision += truePositiveRecommendedTVShow / k;
			}
		}
		return groundTruth.size() == 0 ? 0.0d : averagePrecision / groundTruth.size();
	}
}
