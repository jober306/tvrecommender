package evaluator.metric;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import data.TVDataSetFixture;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

public class NoveltyTest extends TVDataSetFixture{
	
	Novelty<User, TVProgram> novelty;
	
	@Test
	public void evaluateTest() {
		novelty = new Novelty<>();
		Recommendations<User, TVProgram> recommendations = new Recommendations<>(user1, Arrays.asList(program11, program25));
		double expectedResult = 2.0;
		double actualResult = novelty.evaluate(recommendations, evaluationContext);
		assertEquals(expectedResult, actualResult, 0.001d);
	}
	
	@Test
	public void evaluateNeverSeenProgramTest() {
		novelty = new Novelty<>();
		Recommendations<User, TVProgram> recommendations = new Recommendations<>(user1, Arrays.asList(program12));
		double expectedResult = 3/2.0;
		double actualResult = novelty.evaluate(recommendations, evaluationContext);
		assertEquals(expectedResult, actualResult, 0.001d);
	}
}
