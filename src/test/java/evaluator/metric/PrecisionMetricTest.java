package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class PrecisionMetricTest extends RecommendationsFixture{

	
	Precision precision;
	
	@Test
	public void precisionAt5TestNoGoodRecommendationsTest() {
		precision = new Precision(5);
		List<Integer> groundTruth = Arrays.asList(6,7,8,9);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);
		
		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt2TestNoDuplicateRecommendationWithSomeGoodTest() {
		precision = new Precision(2);
		List<Integer> groundTruth = Arrays.asList(1,2);
		double actualResult = precision.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt3TestNoDuplicateRecommendationWithSomeGoodTest() {
		precision = new Precision(3);
		List<Integer> groundTruth = Arrays.asList(1,2);
		double actualResult = precision.evaluate(distinctRecommendations, groundTruth);

		double expectedResult = 2/3.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt2TestAllRecommendationWithSomeGoodTest() {
		precision = new Precision(2);
		List<Integer> groundTruth = Arrays.asList(1,2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 1.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt3TestAllRecommendationWithSomeGoodTest() {
		precision = new Precision(3);
		List<Integer> groundTruth = Arrays.asList(1,2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 2/3.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10TestAllRecommendationWithSomeGoodTest() {
		precision = new Precision(10);
		List<Integer> groundTruth = Arrays.asList(1,2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 3/10.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt10GroundTruthEmptyTest() {
		precision = new Precision(10);
		List<Integer> groundTruth = Arrays.asList();
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 0.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void precisionAt15HigherThanRecommendationsSizeTest() {
		precision = new Precision(15);
		List<Integer> groundTruth = Arrays.asList(1,2);
		double actualResult = precision.evaluate(allRecommendations, groundTruth);

		double expectedResult = 3/10.0d;
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@After
	public void tearDown() {
		this.precision =  null;
	}
}
