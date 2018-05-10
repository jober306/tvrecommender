package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.TVDataSetFixture;
import model.data.TVProgram;
import model.data.User;
import model.data.feature.ChannelTVProgramIdsFeatureExtractor;
import model.data.feature.FeatureExtractor;
import model.measure.distance.CosineDistance;
import model.measure.distance.DistanceMeasure;
import model.recommendation.Recommendations;

public class DiversityTest extends TVDataSetFixture{
	
	FeatureExtractor<? super TVProgram, ?> featureExtractor;
	DistanceMeasure distanceMeasure;
	
	Diversity<User, TVProgram> diversity;
	
	@Before
	public void SetUp(){
		featureExtractor = ChannelTVProgramIdsFeatureExtractor.instance();
		distanceMeasure = CosineDistance.instance();
		diversity = new Diversity<>(featureExtractor, distanceMeasure);
	}
	
	@Test
	public void evaluateEmptyRecommendations() {
		Recommendations<User, TVProgram> emptyRecommendations = new Recommendations<>(user1, Collections.emptyList());
		double expectedResult = 0.0d;
		double actualResult = diversity.evaluate(emptyRecommendations, evaluationContext);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void evaluateAllTheSameRecommendations() {
		Recommendations<User, TVProgram> emptyRecommendations = new Recommendations<>(user1, Arrays.asList(program11, program11, program11));
		double expectedResult = 0.0d;
		double actualResult = diversity.evaluate(emptyRecommendations, evaluationContext);
		assertEquals(expectedResult, actualResult, 0.00001d);
	}
	
	@Test
	public void evaluateDifferentsRecommendations() {
		Recommendations<User, TVProgram> emptyRecommendations = new Recommendations<>(user1, Arrays.asList(program11, program23, program23));
		double distanceDiffTVProgram = distanceMeasure.calculate(featureExtractor.extractFeaturesFromProgram(program11), featureExtractor.extractFeaturesFromProgram(program23));
		double expectedResult = distanceDiffTVProgram * 4 / 6;
		double actualResult = diversity.evaluate(emptyRecommendations, evaluationContext);
		assertEquals(expectedResult, actualResult, 0.00001d);
	}
	
	@After
	public void tearDown() {
		diversity = null;
	}
}
