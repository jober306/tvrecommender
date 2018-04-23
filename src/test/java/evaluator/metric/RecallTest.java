package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.spark_project.guava.collect.Sets;

import data.TVProgram;

public class RecallTest extends RecommendationsFixture{

	Recall recall;
	
	@Test
	public void recallAt5TestNoGoodRecommendationsTest() {
		recall = new Recall(5);
		Set<TVProgram> groundTruth = Sets.newHashSet(tvShow11, tvShow12, tvShow13, tvShow14, tvShow15);
		double actualResult = recall.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt2TestNoDuplicateRecommendationWithSomeGoodTest() {
		recall = new Recall(2);
		Set<TVProgram> groundTruth = Sets.newHashSet(tvShow1, tvShow2);
		double actualResult = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt3TestNoDuplicateRecommendationWithSomeGoodTest() {
		recall = new Recall(3);
		Set<TVProgram> groundTruth = Sets.newHashSet(tvShow3, tvShow4);
		double actualResult = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1/2.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt10TestAllRecommendationWithSomeGoodTest() {
		recall = new Recall(10);
		Set<TVProgram> groundTruth = Sets.newHashSet(tvShow2, tvShow11);
		double actualResult = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d / 2;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10GroundTruthEmptyTest() {
		recall = new Recall(10);
		Set<TVProgram> groundTruth = Collections.emptySet();
		double actualResult = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt15HigherThanRecommendationsSizeTest() {
		recall = new Recall(200);
		Set<TVProgram> groundTruth = Sets.newHashSet(tvShow1, tvShow2);
		double actualResult = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	} 
}
