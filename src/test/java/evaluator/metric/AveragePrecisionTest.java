package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AveragePrecisionTest extends RecommendationsFixture{
	
	AveragePrecision avgPrecision;
	
	@Test
	public void averagePrecisionAt3NoGoodRecommendations() {
		avgPrecision = new AveragePrecision(3);
		List<Integer> groundTruth = Arrays.asList(6,7,8,9);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt3OneGoodRecommendation() {
		avgPrecision = new AveragePrecision(3);
		List<Integer> groundTruth = Arrays.asList(3,7,8,9);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt3OneGoodRecommendationBigGroundTruthWithDuplicate() {
		avgPrecision = new AveragePrecision(3);
		List<Integer> groundTruth = Arrays.asList(3,7,8,9,9,9,9,9);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt5GroundTruthEmptyTest() {
		avgPrecision = new AveragePrecision(5);
		List<Integer> groundTruth = Arrays.asList();
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt15HigherThanRecommendations() {
		avgPrecision = new AveragePrecision(15);
		List<Integer> groundTruth = Arrays.asList(3,7,8,9);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
}
