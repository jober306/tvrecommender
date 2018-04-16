package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import data.TVProgram;

public class AveragePrecisionTest extends RecommendationsFixture{
	
	AveragePrecision avgPrecision;
	
	@Test
	public void averagePrecisionAt3NoGoodRecommendations() {
		avgPrecision = new AveragePrecision(3);
		List<TVProgram> groundTruth = Arrays.asList(tvShow11, tvShow12, tvShow13, tvShow14, tvShow15);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt3OneGoodRecommendation() {
		avgPrecision = new AveragePrecision(3);
		List<TVProgram> groundTruth = Arrays.asList(tvShow3,tvShow12, tvShow13, tvShow14);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt3OneGoodRecommendationBigGroundTruthWithDuplicate() {
		avgPrecision = new AveragePrecision(3);
		List<TVProgram> groundTruth = Arrays.asList(tvShow3,tvShow12, tvShow13, tvShow14,tvShow14, tvShow14, tvShow14, tvShow14);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 8.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt5GroundTruthEmptyTest() {
		avgPrecision = new AveragePrecision(5);
		List<TVProgram> groundTruth = Arrays.asList();
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt1GoodRecommendationsAt2Test() {
		avgPrecision = new AveragePrecision(1);
		List<TVProgram> groundTruth = Arrays.asList(tvShow12,tvShow3, tvShow13, tvShow14);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt15HigherThanRecommendations() {
		avgPrecision = new AveragePrecision(15);
		List<TVProgram> groundTruth = Arrays.asList(tvShow3,tvShow12, tvShow13, tvShow14);
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
}
