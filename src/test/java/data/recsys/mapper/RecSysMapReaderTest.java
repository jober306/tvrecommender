package data.recsys.mapper;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class RecSysMapReaderTest {
	
	RecSysMapReader reader;
	
	@Before
	public void setUp(){
		RecSysMapCreator mapCreator = new RecSysMapCreator();
		Set<Integer> eventIds = Sets.newHashSet(Arrays.asList(1,2,3,4,5,6));
		Set<Integer> userIds = Sets.newHashSet(Arrays.asList(7,8,9,10));
		Set<Integer> programIds = Sets.newHashSet(Arrays.asList(11,12,13));
		mapCreator.createEventIDToIDMap(eventIds);
		mapCreator.createUserIDToIDMap(userIds);
		mapCreator.createProgramIDToIDMap(programIds);
		reader = new RecSysMapReader(mapCreator.getFileNames());
	}
	
	@Test
	public void fileNamesAreCorrect(){
		assertEquals("userIDToIDMap1.txt", reader.getUserIDMapFileName());
		assertEquals("programIDToIDMap1.txt", reader.getProgramIDMapFileName());
		assertEquals("eventIDToIDMap1.txt", reader.getEventIDMapFileName());
	}
	
	@Test
	public void mapReadCorrectlyTest() {
		assertEquals(new Integer(2), reader.mapUserIDtoID(9));
	}
	
	@After
	public void tearDown(){
		reader.close();
	}

}
