package evaluator.metric;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.mllib.linalg.Vector;

import data.EvaluationContext;
import model.data.TVProgram;
import model.data.User;
import model.data.feature.FeatureExtractor;
import model.measure.distance.DistanceMeasure;
import model.recommendation.Recommendations;

/**
 * The Diversity metric class. It calculates the average intra distance of a list of recommendations.
 * The distance between two recommendation vectors is calculated using cosine similarity.
 * @author Jonathan Bergeron
 *
 * @param <U> The type of user
 * @param <P> The type of tv program
 */
public class Diversity<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{
	
	final FeatureExtractor<? super P, ?> featureExtractor;
	final DistanceMeasure distanceMeasure;
	
	/**
	 * Constructor of this class. It takes as input a feature extractor that will be used to transform recommendations
	 * into vector. It then uses the given distance measure to calculate distance between vectors. 
	 * @param featureExtractor The feature extractor.
	 * @param distanceMeasure The distance measure.
	 */
	public Diversity(FeatureExtractor<? super P, ?> featureExtractor, DistanceMeasure distanceMeasure) {
		this.featureExtractor = featureExtractor;
		this.distanceMeasure = distanceMeasure;
	}
	
	@Override
	public String name() {
		return "Diversity";
	}

	@Override
	public double evaluate(Recommendations<U, P> recommendations, EvaluationContext<U, P, ?> evaluationContext) {
		final int recommendationsSize = recommendations.size();
		final List<Vector> recommendationsVector = recommendations.stream().map(featureExtractor::extractFeaturesFromProgram).collect(Collectors.toList());
		double sum = 0.0d;
		for(int i = 0; i < recommendationsSize; i++) {
			for(int j = i+1; j < recommendationsSize; j++) {
				sum += distanceMeasure.calculate(recommendationsVector.get(i), recommendationsVector.get(j));
			}
		}
		return recommendationsSize == 0 ? 0.0d : 2 * sum / (recommendationsSize * (recommendationsSize -1));
	}
}
