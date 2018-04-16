package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import data.TVProgram;

public class PrecisionMetricTest extends RecommendationsFixture{

	
	Precision precision;
	
	@Test
	public void precisionAt5TestNoGoodRecommendationsTest() {
		precision = new Precision(5);
		List<TVProgram> groundTruth = Arrays.asList(tvShow11, tvShow12, tvShow13, tvShow14, tvShow15);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt2TestNoDuplicateRecommendationWithSomeGoodTest() {
		precision = new Precision(2);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = precision.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt3TestNoDuplicateRecommendationWithSomeGoodTest() {
		precision = new Precision(3);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = precision.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 2/3.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt2TestAllRecommendationWithSomeGoodTest() {
		precision = new Precision(2);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt3TestAllRecommendationWithSomeGoodTest() {
		precision = new Precision(3);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 2/3.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10TestAllRecommendationWithSomeGoodTest() {
		precision = new Precision(10);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 2/10.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10GroundTruthEmptyTest() {
		precision = new Precision(10);
		List<TVProgram> groundTruth = Arrays.asList();
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt15HigherThanRecommendationsSizeTest() {
		precision = new Precision(15);
		List<TVProgram> groundTruth = Arrays.asList(tvShow1, tvShow2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 2/10.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@After
	public void tearDown() {
		this.precision =  null;
	}
}
