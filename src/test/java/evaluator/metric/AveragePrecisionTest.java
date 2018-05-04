package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import model.data.TVProgram;
import model.data.User;

public class AveragePrecisionTest extends RecommendationsFixture{
	
	AveragePrecision<User, TVProgram> avgPrecision;
	
	@Test
	public void averagePrecisionAt3NoGoodRecommendations() {
		avgPrecision = new AveragePrecision<>(3);
		Set<TVProgram> groundTruth = ImmutableSet.copyOf(Arrays.asList(tvShow11, tvShow12, tvShow13, tvShow14, tvShow15));
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt3OneGoodRecommendation() {
		avgPrecision = new AveragePrecision<>(3);
		Set<TVProgram> groundTruth = ImmutableSet.copyOf(Arrays.asList(tvShow3,tvShow12, tvShow13, tvShow14));
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt3OneGoodRecommendationBigGroundTruthWithDuplicate() {
		avgPrecision = new AveragePrecision<>(3);
		Set<TVProgram> groundTruth = ImmutableSet.copyOf(Arrays.asList(tvShow3,tvShow12, tvShow13, tvShow14,tvShow14, tvShow14, tvShow14, tvShow14));
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt5GroundTruthEmptyTest() {
		avgPrecision = new AveragePrecision<>(5);
		Set<TVProgram> groundTruth = Collections.emptySet();
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt1GoodRecommendationsAt2Test() {
		avgPrecision = new AveragePrecision<>(1);
		Set<TVProgram> groundTruth = ImmutableSet.copyOf(Arrays.asList(tvShow12,tvShow3, tvShow13, tvShow14));
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void averagePrecisionAt15HigherThanRecommendations() {
		avgPrecision = new AveragePrecision<>(15);
		Set<TVProgram> groundTruth = ImmutableSet.copyOf(Arrays.asList(tvShow3,tvShow12, tvShow13, tvShow14));
		double actualResult = avgPrecision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 1/ (3.0d * 4.0d);
		assertEquals(expectedResult, actualResult, 0.0d);
	}
}
