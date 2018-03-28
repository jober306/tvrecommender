package evaluator.result;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SingleUserResultTest {
	
	final static int USER_ID = 10;
	final static double SCORE = 0.45d;
	
	SingleUserResult result;
	
	
	@Before
	public void setUp() {
		result = new SingleUserResult(USER_ID, SCORE);
	}
	
	@Test
	public void evaluationResultInstantiatedCorrectlyTest() {
		assertEquals(USER_ID, result.userId());
		assertEquals(SCORE, result.score(), 0.0d);
	}
	
	@After
	public void tearDown() {
		result = null;
	}
}
