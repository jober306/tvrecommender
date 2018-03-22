package evaluator.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EvaluationResultTest {
	
	final static int USER_ID = 10;
	final static double SCORE = 0.45d;
	
	EvaluationResult result;
	
	
	@Before
	public void setUp() {
		result = new EvaluationResult(USER_ID, SCORE);
	}
	
	@Test
	public void evaluationResultInstantiatedCorrectlyTest() {
		assertEquals(USER_ID, result.userId());
		assertEquals(SCORE, result.score(), 0.0d);
	}
	
	@Test
	public void evaluationResultEqualsTest() {
		EvaluationResult otherResult = new EvaluationResult(USER_ID, SCORE);
		assertEquals(result, otherResult);
	}
	
	@Test
	public void evaluationResultNotEqualsTest() {
		EvaluationResult otherResult = new EvaluationResult(1, SCORE);
		assertNotEquals(result, otherResult);
	}
	
	@After
	public void tearDown() {
		result = null;
	}
}
