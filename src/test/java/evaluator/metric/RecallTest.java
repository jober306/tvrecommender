package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import data.TVProgram;

public class RecallTest extends RecommendationsFixture{

	Recall recall;
	
	@Test
	public void recallAt5TestNoGoodRecommendationsTest() {
		recall = new Recall(5);
		List<TVProgram> groundTruth = Arrays.asList(tvShow11, tvShow12, tvShow13, tvShow14, tvShow15);
		double actualResult = recall.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt2TestNoDuplicateRecommendationWithSomeGoodTest() {
		recall = new Recall(2);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt3TestNoDuplicateRecommendationWithSomeGoodTest() {
		recall = new Recall(3);
		List<TVProgram> groundTruth = Arrays.asList(tvShow3, tvShow4);
		double actualResult = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1/2.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt10TestAllRecommendationWithSomeGoodTest() {
		recall = new Recall(10);
		List<TVProgram> groundTruth = Arrays.asList(tvShow2, tvShow11);
		double actualResult = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d / 2;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10GroundTruthEmptyTest() {
		recall = new Recall(10);
		List<TVProgram> groundTruth = Arrays.asList();
		double actualResult = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt15HigherThanRecommendationsSizeTest() {
		recall = new Recall(200);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	} 
}
