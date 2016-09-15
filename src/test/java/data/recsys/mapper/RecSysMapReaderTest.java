package data.recsys.mapper;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RecSysMapReaderTest {
	
	RecSysMapReader reader;
	
	@Before
	public void setUp(){
		RecSysMapCreator mapCreator = new RecSysMapCreator();
		List<Integer> eventIds = Arrays.asList(1,2,3,4,5,6);
		List<Integer> userIds = Arrays.asList(7,8,9,10);
		List<Integer> programIds = Arrays.asList(11,12,13);
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
