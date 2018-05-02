package data;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

public class GroundModelTest extends TVDataSetFixture{
	
	GroundModel<User, TVProgram, TVEvent<User, TVProgram>> model;
	GroundModel<User, TVProgram, TVEvent<User, TVProgram>> emptyModel;
	
	@Before
	public void setUp(){
		model = new GroundModel<>(dataset);
		emptyModel = new GroundModel<>(emptyDataset);
	}
	
	@Test
	public void probabilityNonExistingTVProgramIsChosenTest(){
		double expectedProbability = 0.0d;
		TVProgram neverSeenTVProgram = new TVProgram(baseTime, baseTime.plusHours(1), -1, -1);
		double actualProbability = model.probabilityTVProgramIsChosen(neverSeenTVProgram);
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilityTVProgramIsChosenEmptyDataSetTest(){
		double expectedProbability = 0.0d;
		double actualProbability = emptyModel.probabilityTVProgramIsChosen(program11);
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilitySeenTVProgramIsChosenTest(){
		double expectedProbability = 1.0d / 6.0d;
		double actualProbability = model.probabilityTVProgramIsChosen(program12);
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilityNonExistingTVProgramIsChosenByUserTest(){
		double expectedProbability = 0.0d;
		TVProgram neverSeenTVProgram = new TVProgram(baseTime, baseTime.plusHours(1), -1, -1);
		double actualProbability = model.probabilityTVProgramIsChosenByUser(neverSeenTVProgram, new User(1));
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilityTVProgramIsChosenByNonExistingUserTest(){
		double expectedProbability = 0.0d;
		User nonExistingUser = new User(-1);
		double actualProbability = model.probabilityTVProgramIsChosenByUser(program12, nonExistingUser);
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilityTVProgramIsChosenByUserEmptyDataSetTest(){
		double expectedProbability = 0.0d;
		double actualProbability = emptyModel.probabilityTVProgramIsChosenByUser(program11, new User(1));
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilitySeenTVProgramIsChosenByUserTest(){
		double expectedProbability = 1.0d / 3.0d;
		double actualProbability = model.probabilityTVProgramIsChosenByUser(program12, new User(2));
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilityNonExistingTVProgramIsKnownTest(){
		double expectedProbability = 0.0d;
		TVProgram neverSeenTVProgram = new TVProgram(baseTime, baseTime.plusHours(1), -1, -1);
		double actualProbability = model.probabilityTVProgramIsKnown(neverSeenTVProgram);
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilityTVProgramIsKnownEmptyDataSetTest(){
		double expectedProbability = 0.0d;
		double actualProbability = emptyModel.probabilityTVProgramIsChosen(program11);
		assertEquals(expectedProbability, actualProbability, 0.0d);
	}
	
	@Test
	public void probabilitySeenTVProgramIsKnownTest(){
		double expectedProbability = 2.0d / 3.0d;
		double actualProbability1 = model.probabilityTVProgramIsKnown(program25);
		double actualProbability2 = model.probabilityTVProgramIsKnown(program45);
		assertEquals(expectedProbability, actualProbability1, 0.0d);
		assertEquals(expectedProbability, actualProbability2, 0.0d);
	}
	
	@After
	public void tearDown(){
		model = null;
		emptyModel = null;
	}
}
