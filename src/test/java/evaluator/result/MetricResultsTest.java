package evaluator.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MetricResultsTest {
	
	static final SingleUserResult USER_RESULT1 = new SingleUserResult(1, 0.5d);
	static final SingleUserResult USER_RESULT2 = new SingleUserResult(2, 0.6d);
	static final SingleUserResult USER_RESULT3 = new SingleUserResult(3, 0.7d);
	static final SingleUserResult USER_RESULT4 = new SingleUserResult(4, 0.8d);

	
	Map<Integer, Double> usersScore;
	List<SingleUserResult> singleUserResults;
	
	@Before
	public void setUp(){
		usersScore = new HashMap<Integer, Double>();
		usersScore.put(USER_RESULT1.userId(), USER_RESULT1.score());
		usersScore.put(USER_RESULT2.userId(), USER_RESULT2.score());
		usersScore.put(USER_RESULT3.userId(), USER_RESULT3.score());
		usersScore.put(USER_RESULT4.userId(), USER_RESULT4.score());
		singleUserResults = Arrays.asList(USER_RESULT1, USER_RESULT2, USER_RESULT3, USER_RESULT4);
	}
	
	@Test
	public void metricResultsInitializedWithMapTest(){
		MetricResults metricResults = new MetricResults(usersScore);
		Map<Integer, Double> expectedResults = new HashMap<Integer, Double>(usersScore);
		assertEquals(expectedResults, metricResults.usersScore());
	}
	
	@Test
	public void metricResultsInitializedWithMapNotSeenUserTest(){
		MetricResults metricResults = new MetricResults(usersScore);
		assertTrue(!metricResults.userScore(5).isPresent());
	}
	
	@Test
	public void metricResultsInitializedWithListTest(){
		MetricResults metricResults = new MetricResults(singleUserResults);
		assertEquals(usersScore, metricResults.usersScore());
	}
	
	@Test
	public void metricResultsInitializedWithListMultipleSameUserTest(){
		this.singleUserResults = new ArrayList<>(singleUserResults);
		singleUserResults.add(new SingleUserResult(1, 1.0d));
		singleUserResults.add(new SingleUserResult(1, 1.5d));
		MetricResults metricResults = new MetricResults(singleUserResults);
		double expectedResult = 1.0d;
		double actualResult = metricResults.userScore(1).get();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@After
	public void tearDown(){
		usersScore = null;
		singleUserResults = null;
	}
}
