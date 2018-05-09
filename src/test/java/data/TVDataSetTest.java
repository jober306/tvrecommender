package data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

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
		TVEvent<User, TVProgram> deepCopyEvent1 = new TVEvent<>(event1.watchTime(), event1.program(), event1.user(), event1.eventID(), event1.watchDuration());
		assertTrue(dataset.contains(deepCopyEvent1));
	}
	
	@Test
	public void doesNotContainEventInEmptyDatasetTest(){
		assertTrue(!emptyDataset.contains(event1));
	}
	
	@Test
	public void getProgramIndexesSeenByExistingUserOnValidDatasetTest(){
		Set<Integer> expectedIndexSeenUser1 = ImmutableSet.copyOf(Arrays.asList(1,3));
		Set<Integer> actualIndexSeenUser1 = dataset.tvProgramIndexesSeenByUser(new User(1));
		assertEquals(expectedIndexSeenUser1, actualIndexSeenUser1);
	}
	
	@Test
	public void getProgramSeenByExistingUserOnValidDatasetTest(){
		Set<TVProgram> expectedProgramSeenUser1 = ImmutableSet.copyOf(Arrays.asList(program11, program23));
		Set<TVProgram> actualProgramSeenUser1 = dataset.tvProgramSeenByUser(new User(1));
		assertEquals(expectedProgramSeenUser1, actualProgramSeenUser1);
	}
	
	@Test
	public void programSeenByNonExistingUserShouldBeEmptyTest(){
		Set<Integer> actualSet = dataset.tvProgramIndexesSeenByUser(new User(10));
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void countOnValidDatasetTest(){
		int expectedCount = 6;
		int actualCount = (int)dataset.numberOfTvEvents();
		assertEquals(expectedCount, actualCount);
	}
	
	@Test
	public void countOnEmptyDatasetShouldBeZeroTest(){
		int expectedCount = 0;
		int actualCount = (int)emptyDataset.numberOfTvEvents();
		assertEquals(expectedCount, actualCount);
	}
	
	@Test
	public void splitValidDatasetIntersectionIsEmptyTest(){
		double[] ratios = { 0.17, 0.43, 0.40 };
		Set<TVDataSet<User, TVProgram, TVEvent<User, TVProgram>>> splittedDataSet = dataset.splitTVEventsRandomly(ratios);
		JavaRDD<TVEvent<User, TVProgram>> intersection = splittedDataSet.stream().map(TVDataSet::events).reduce(JavaRDD::intersection).get();
		assertTrue(intersection.isEmpty());
	}
	
	@Test
	public void splitValidDatasetUnionIsOriginalDatasetTest(){
		double[] ratios = { 0.17, 0.43, 0.40 };
		Set<TVDataSet<User, TVProgram, TVEvent<User, TVProgram>>> splittedDataSet = dataset.splitTVEventsRandomly(ratios);
		JavaRDD<TVEvent<User, TVProgram>> unions = splittedDataSet.stream().map(TVDataSet::events).reduce(JavaRDD::union).get();
		Set<TVEvent<User, TVProgram>> expectedTVEvents = ImmutableSet.copyOf(Arrays.asList(event1, event2, event3, event4, event5, event6));
		Set<TVEvent<User, TVProgram>> actualTVEvents = ImmutableSet.copyOf(unions.collect());
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
		Set<Integer> expectedSet = ImmutableSet.copyOf(Arrays.asList(1,2,3));
		Set<Integer> actualSet = dataset.allUserIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allUserIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.allUserIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void allTVProgramIdsOnValidDatasetTest(){
		Set<Integer> expectedSet = ImmutableSet.copyOf(Arrays.asList(1,2,3,4,5));
		Set<Integer> actualSet = dataset.allProgramIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allTVProgramIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.allProgramIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void allEventIdsOnValidDatasetTest(){
		Set<Integer> expectedSet = ImmutableSet.copyOf(Arrays.asList(0,1,2,3,4,5));
		Set<Integer> actualSet = dataset.allEventIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allEventIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Integer> actualSet = emptyDataset.allEventIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void allChannelIdsOnValidDatasetTest(){
		Set<Short> expectedSet = ImmutableSet.copyOf(Arrays.asList((short)1, (short)2, (short)3, (short)4));
		Set<Short> actualSet = dataset.allChannelIds();
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void allChannelIdsOnEmptyDatasetShouldBeEmptyTest(){
		Set<Short> actualSet = emptyDataset.allChannelIds();
		assertTrue(actualSet.isEmpty());
	}
	
	@Test
	public void numberOfUsersOnValideDatasetTest(){
		int expectedNumberOfUsers = 3;
		int actualNumberOfUsers = dataset.numberOfUsers();
		assertEquals(expectedNumberOfUsers, actualNumberOfUsers);
	}
	
	@Test
	public void numberOfUsersOnEmptyDatasetTest(){
		int expectedNumberOfUsers = 0;
		int actualNumberOfUsers = emptyDataset.numberOfUsers();
		assertEquals(expectedNumberOfUsers, actualNumberOfUsers);
	}
	
	@Test
	public void numberOfTvShowsOnValideDatasetTest(){
		int expectedNumberOfTVPrograms = 6;
		int actualNumberOfTvPrograms = dataset.numberOfTvPrograms();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTVShowsOnEmptyDatasetTest(){
		int expectedNumberOfTVPrograms = 0;
		int actualNumberOfTvPrograms = emptyDataset.numberOfTvPrograms();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTvShowIndexesOnValideDatasetTest(){
		int expectedNumberOfTVPrograms = 5;
		int actualNumberOfTvPrograms = dataset.numberOfTvShowIds();
		assertEquals(expectedNumberOfTVPrograms, actualNumberOfTvPrograms);
	}
	
	@Test
	public void numberOfTVShowIndexesOnEmptyDatasetTest(){
		int expectedNumberOfTVPrograms = 0;
		int actualNumberOfTvPrograms = emptyDataset.numberOfTvShowIds();
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
