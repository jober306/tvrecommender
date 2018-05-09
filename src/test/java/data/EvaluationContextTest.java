package data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

public class EvaluationContextTest extends TVDataSetFixture{
	
	@Test
	public void trainingSetCreatedProperlyTest() {
		TVDataSet<User, TVProgram, TVEvent<User, TVProgram>> trainingSet = evaluationContext.getTrainingSet();
		int expectedSize = 4;
		int actualSize = (int) trainingSet.numberOfTvEvents();
		assertEquals(expectedSize, actualSize);
		assertTrue(trainingSet.contains(event1));
		assertTrue(trainingSet.contains(event3));
		assertTrue(trainingSet.contains(event4));
		assertTrue(trainingSet.contains(event6));
	}
	
	@Test
	public void testSetCreatedProperlyTest() {
		TVDataSet<User, TVProgram, TVEvent<User, TVProgram>> testSet = evaluationContext.getTestSet();
		int expectedSize = 2;
		int actualSize = (int)testSet.numberOfTvEvents();
		assertEquals(expectedSize, actualSize);
		assertTrue(testSet.contains(event2));
		assertTrue(testSet.contains(event5));
	}
	
	@Test
	public void groundTruthCreatedProperlyTest() {
		Map<User, Set<TVProgram>> groundTruth = evaluationContext.getGroundTruth();
		int expectedSize = 2;
		int actualSize = groundTruth.size();
		assertEquals(expectedSize, actualSize);
		assertTrue(groundTruth.containsKey(new User(2)));
		assertTrue(groundTruth.containsKey(new User(3)));
		Set<TVProgram> expectedUser2GroundTruth = new HashSet<TVProgram>(Arrays.asList(program12));
		Set<TVProgram> expectedUser3GroundTruth = new HashSet<TVProgram>(Arrays.asList(program45));
		Set<TVProgram> actualUser2GroundTruth = groundTruth.get(new User(2));
		Set<TVProgram> actualUser3GroundTruth = groundTruth.get(new User(3));
		assertEquals(expectedUser2GroundTruth, actualUser2GroundTruth);
		assertEquals(expectedUser3GroundTruth, actualUser3GroundTruth);
	}
}
