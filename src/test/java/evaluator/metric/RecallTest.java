package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import evaluator.result.SingleUserResult;

public class RecallTest extends RecommendationsFixture{

	Recall recall;
	
	@Test
	public void recallAt5TestNoGoodRecommendationsTest() {
		recall = new Recall(5);
		List<Integer> groundTruth = Arrays.asList(6,7,8,9);
		SingleUserResult result = recall.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt2TestNoDuplicateRecommendationWithSomeGoodTest() {
		recall = new Recall(2);
		List<Integer> groundTruth = Arrays.asList(1,2);
		SingleUserResult result = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1.0d;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt3TestNoDuplicateRecommendationWithSomeGoodTest() {
		recall = new Recall(3);
		List<Integer> groundTruth = Arrays.asList(3,4);
		SingleUserResult result = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1/2.0d;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt3TestNoDuplicateRecommendationWithDuplicateGroundTruth() {
		recall = new Recall(3);
		List<Integer> groundTruth = Arrays.asList(3,4,4,4);
		SingleUserResult result = recall.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1/2.0d;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void recallAt10TestAllRecommendationWithSomeGoodTest() {
		recall = new Recall(10);
		List<Integer> groundTruth = Arrays.asList(2,5);
		SingleUserResult result = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d / 2;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10GroundTruthEmptyTest() {
		recall = new Recall(10);
		List<Integer> groundTruth = Arrays.asList();
		SingleUserResult result = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 0.0d;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt15HigherThanRecommendationsSizeTest() {
		recall = new Recall(200);
		List<Integer> groundTruth = Arrays.asList(1,2);
		SingleUserResult result = recall.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d;
		double actualResult = result.score();
		assertEquals(expectedResult, actualResult, 0.0d);
	} 
}
