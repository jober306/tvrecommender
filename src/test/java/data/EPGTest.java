package data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import model.data.TVProgram;

public class EPGTest extends TVDataSetFixture{

	@Test
	public void getListProgramsAtWatchTimeNoProgramEndingTest() {
		List<TVProgram> results = epg.getListProgramsAtWatchTime(baseTime
				.plusMinutes(55));
		int expectedSize = 4;
		List<TVProgram> expectedResults = Arrays.asList(program11, program22,
				program32, program44);
		assertEquals(expectedSize, results.size());
		assertEquals(results, expectedResults);
	}

	@Test
	public void getListProgramAtWatchTimeWithProgramEndingTest() {
		List<TVProgram> results = epg.getListProgramsAtWatchTime(baseTime
				.plusMinutes(90));
		int expectedSize = 4;
		List<TVProgram> expectedResults = Arrays.asList(program12, program25,
				program34, program46);
		assertEquals(expectedSize, results.size());
		assertEquals(expectedResults, results);
	}

	@Test
	public void getListProgramBetweenTimeNoProgramEnding() {
		List<TVProgram> results = epg.getListProgramsBetweenTimes(
				baseTime.plusMinutes(20), baseTime.plusMinutes(25));
		int expectedSize = 4;
		List<TVProgram> expectedResults = Arrays.asList(program11, program23,
				program32, program44);
		assertEquals(expectedSize, results.size());
		assertEquals(expectedResults, results);
	}

	@Test
	public void getListProgramBetweenTimeWithProgramEnding() {
		List<TVProgram> results = epg.getListProgramsBetweenTimes(
				baseTime.plusMinutes(30), baseTime.plusMinutes(90));
		int expectedSize = 6;
		List<TVProgram> expectedResults = Arrays.asList(program11, program13,
				program22, program32, program34, program44);
		assertEquals(expectedSize, results.size());
		assertEquals(expectedResults, results);
	}

	@Test
	public void getListProgramBetweenTimeNoprogram() {
		List<TVProgram> results = epg.getListProgramsBetweenTimes(
				baseTime.plusDays(3), baseTime.plusDays(3));
		assertEquals(0, results.size());

	}
}
