package evaluator.metric;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import data.GroundModel;
import data.TVDataSetFixture;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

public class NoveltyTest extends TVDataSetFixture{
	
	Novelty<User, TVProgram> novelty;
	
	@Test
	public void evaluateTest() {
		novelty = new Novelty<>(new GroundModel<>(dataset));
		Recommendations<User, TVProgram> recommendations = new Recommendations<>(user1, Arrays.asList(program11, program12));
		double expectedResult = 3.584;
		double actualResult = novelty.evaluate(recommendations, new HashSet<>());
	}
}
