package data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import com.google.common.collect.Sets;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;


public class TVDataSetTest extends TVDataSetFixture{
	
	@Test
	public void isNotEmptyOnValidDatasetTest(){
		assertTrue(!dataset.isEmpty());
	}
	
	@Test
	public void isEmptyOnEmptyDatasetTest(){
		assertTrue(emptyDataset.isEmpty());
	}
	
	@Test
	public void containsShallowCopyOfEventTest(){
		assertTrue(dataset.contains(event1));
	}
	
	@Test
	public void containsDeepCopyOfEventTest(){
		TVEvent<User, TVProgram> deepCopyEvent1 = new TVEvent<>(event1.getWatchTime(), event1.getProgram(), event1.getUser(), event1.getEventID(), event1.getDuration());
		assertTrue(dataset.contains(deepCopyEvent1));
	}
	
	@Test
	public void doesNotContainEventInEmptyDatasetTest(){
		assertTrue(!emptyDataset.contains(event1));
	}
	
	@Test
	public void getProgramIndexesSeenByExistingUserOnValidDatasetTest(){
		Set<Integer> expectedIndexSeenUser1 = Sets.newHashSet(Arrays.asList(1,3));
		Set<Integer> actualIndexSeenUser1 = dataset.getTvProgramIndexesSeenByUser(1);
		assertEquals(expectedIndexSeenUser1, actualIndexSeenUser1);
	}
	
	@Test
	public void getProgramSeenByExistingUserOnValidDatasetTest(){
		Set<TVProgram> expectedProgramSeenUser1 = Sets.newHashSet(program11, program23);
		Set<TVProgram> actualProgramSeenUser1 = dataset.getTVProgramSeenByUser(1);
		assertEquals(expectedProgramSeenUser1, actualProgramSeenUser1);
	}
	
	@Test
	public void programSeenByNonExistingUserShouldBeEmptyTest(){
		Set<Integer> actualSet = dataset.getTvProgramIndexesSeenByUser(10);
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void countOnValidDatasetTest(){
		int expectedCount = 6;
		int actualCount = dataset.count();
		assertEquals(expectedCount, actualCount);
	}
	
	@Test
	public void countOnEmptyDatasetShouldBeZeroTest(){
		int expectedCount = 0;
		int actualCount = emptyDataset.count();
		assertEquals(expectedCount, actualCount);
	}
	
	@Test
	public void splitValidDatasetIntersectionIsEmptyTest(){
		double[] ratios = { 0.17, 0.43, 0.40 };
		Set<TVDataSet<User, TVProgram, TVEvent<User, TVProgram>>> splittedDataSet = dataset.splitTVEventsRandomly(ratios);
		JavaRDD<TVEvent<User, TVProgram>> intersection = splittedDataSet.stream().map(TVDataSet::getEventsData).reduce(JavaRDD::intersection).get();
		assertTrue(intersection.isEmpty());
	}
	
	@Test
	public void splitValidDatasetUnionIsOriginalDatasetTest(){
		double[] ratios = { 0.17, 0.43, 0.40 };
		Set<TVDataSet<User, TVProgram, TVEvent<User, TVProgram>>> splittedDataSet = dataset.splitTVEventsRandomly(ratios);
		JavaRDD<TVEvent<User, TVProgram>> unions = splittedDataSet.stream().map(TVDataSet::getEventsData).reduce(JavaRDD::union).get();
		Set<TVEvent<User, TVProgram>> expectedTVEvents = Sets.newHashSet(Arrays.asList(event1, event2, event3, event4, event5, event6));
		Set<TVEvent<User, TVProgram>> actualTVEvents = Sets.newHashSet(unions.collect());
		assertEquals(expectedTVEvents, actualTVEvents);
	}
	
	@Test
	public void splitEmptyDatasetAllShouldBeEmptyTest(){
		double[] ratios = { 0.1, 0.5, 0.4 };
		Set<TVDataSet<User, TVProgram, TVEvent<User, TVProgram>>> splittedDataSets = emptyDataset.splitTVEventsRandomly(ratios);
		for(TVDataSet<User, TVProgram, TVEvent<User, TVProgram>> splittedDataset : splittedDataSets){
			assertTrue(splittedDataset.isEmpty());
		}
	}
	
	@Test
	public void allUserIdsOnValidDatasetTest(){
		Set<Integer> expectedSet = Sets.newHashSet(Arrays.asList(1,2,3));
		Set<Integer> actualSet = dataset.getAllUserIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allUserIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.getAllUserIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void allTVProgramIdsOnValidDatasetTest(){
		Set<Integer> expectedSet = Sets.newHashSet(Arrays.asList(1,2,3,4,5));
		Set<Integer> actualSet = dataset.getAllProgramIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allTVProgramIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.getAllProgramIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void allEventIdsOnValidDatasetTest(){
		Set<Integer> expectedSet = Sets.newHashSet(Arrays.asList(0,1,2,3,4,5));
		Set<Integer> actualSet = dataset.getAllEventIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allEventIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.getAllEventIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void allChannelIdsOnValidDatasetTest(){
		Set<Integer> expectedSet = Sets.newHashSet(Arrays.asList(1, 2, 3, 4));
		Set<Integer> actualSet = dataset.getAllChannelIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allChannelIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.getAllChannelIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void numberOfUsersOnValideDatasetTest(){
		int expectedNumberOfUsers = 3;
		int actualNumberOfUsers = dataset.getNumberOfUsers();
		assertEquals(expectedNumberOfUsers, actualNumberOfUsers);
	}
	
	@Test
	public void numberOfUsersOnEmptyDatasetTest(){
		int expectedNumberOfUsers = 0;
		int actualNumberOfUsers = emptyDataset.getNumberOfUsers();
		assertEquals(expectedNumberOfUsers, actualNumberOfUsers);
	}
	
	@Test
	public void numberOfTvShowsOnValideDatasetTest(){
		int expectedNumberOfTVPrograms = 6;
		int actualNumberOfTvPrograms = dataset.getNumberOfTvShows();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTVShowsOnEmptyDatasetTest(){
		int expectedNumberOfTVPrograms = 0;
		int actualNumberOfTvPrograms = emptyDataset.getNumberOfTvShows();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTvShowIndexesOnValideDatasetTest(){
		int expectedNumberOfTVPrograms = 5;
		int actualNumberOfTvPrograms = dataset.getNumberOfTvShowIndexes();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTVShowIndexesOnEmptyDatasetTest(){
		int expectedNumberOfTVPrograms = 0;
		int actualNumberOfTvPrograms = emptyDataset.getNumberOfTvShowIndexes();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTvEventsOnValideDatasetTest(){
		long expectedNumberOfTVEvents = 6;
		long actualNumberOfTvEvents = dataset.numberOfTvEvents();
		assertEquals(expectedNumberOfTVEvents, actualNumberOfTvEvents);
	}
	
	@Test
	public void numberOfTVEventsOnEmptyDatasetTest(){
		long expectedNumberOfTVEvents = 0;
		long actualNumberOfTvEvents = emptyDataset.numberOfTvEvents();
		assertEquals(expectedNumberOfTVEvents, actualNumberOfTvEvents);
	}
	
	@Test
	public void validDatasetStartTimeTest(){
		LocalDateTime expectedStartTime = baseTime;
		LocalDateTime actualStartTime = dataset.startTime();
		assertEquals(expectedStartTime, actualStartTime);
	}
	
	@Test
	public void validDatasetEndTimeTest(){
		LocalDateTime expectedEndTime = baseTime.plusMinutes(135);
		LocalDateTime actualEndTime = dataset.endTime();
		assertEquals(expectedEndTime, actualEndTime);
	}
}
