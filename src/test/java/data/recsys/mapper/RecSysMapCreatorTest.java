package data.recsys.mapper;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RecSysMapCreatorTest {
	
	RecSysMapCreator mapCreator;
	
	@Before
	public void setUp(){
		mapCreator = new RecSysMapCreator();
	}
	
	@Test
	public void testConstructorWithNoFileCreatedYet() {
		assertEquals("userIDToIDMap1.txt", mapCreator.getuserIDToIDMapFileName());
		assertEquals("programIDToIDMap1.txt", mapCreator.getProgramIDToIDMapFileName());
		assertEquals("eventIDToIDMap1.txt", mapCreator.getEventIDToIDMapFileName());
	}
	
	@Test
	public void testConstructorWithFileAlreadyCreated(){
		mapCreator.createEventIDToIDMap(Arrays.asList(1,23,3,4,5,6));
		mapCreator.createUserIDToIDMap(Arrays.asList(1,23,3,4,5,6));
		mapCreator.createProgramIDToIDMap(Arrays.asList(1,23,3,4,5,6));
		RecSysMapCreator secondMapCreator = new RecSysMapCreator();
		assertEquals("userIDToIDMap2.txt", secondMapCreator.getuserIDToIDMapFileName());
		assertEquals("programIDToIDMap2.txt", secondMapCreator.getProgramIDToIDMapFileName());
		assertEquals("eventIDToIDMap2.txt", secondMapCreator.getEventIDToIDMapFileName());
	}
	
	@Test
	public void testGetFileNames(){
		List<String> expectedFileNames = Arrays.asList("userIDToIDMap1.txt", "programIDToIDMap1.txt","eventIDToIDMap1.txt");
		assertEquals(expectedFileNames, Arrays.asList(mapCreator.getFileNames()));
	}
	
	@Test
	public void testMapCreatedCorrectly(){
		List<Integer> eventIds = Arrays.asList(1,2,3,4,5,6);
		List<Integer> userIds = Arrays.asList(7,8,9,10);
		List<Integer> programIds = Arrays.asList(11,12,13);
		mapCreator.createEventIDToIDMap(eventIds);
		mapCreator.createUserIDToIDMap(userIds);
		mapCreator.createProgramIDToIDMap(programIds);
		File[] fileToRead = new File[3];
		fileToRead[0] = new File(mapCreator.getUserIDToIDMapPath());
		fileToRead[1] = new File(mapCreator.getProgramIDToIDMapPath());
		fileToRead[2] = new File(mapCreator.getEventIDToIDMapPath());
		for(int i = 0; i < 3; i++){
			List<Integer> readOriginalIds = new ArrayList<Integer>(); 
			try (BufferedReader br = new BufferedReader(new FileReader(fileToRead[i]))){
				String line = "";
				int index = 0;
				while((line = br.readLine()) != null){
					readOriginalIds.add(Integer.parseInt(line.split(RecSysMapCreator.MAP_DELIMITER)[0]));
					assertEquals(index, Integer.parseInt(line.split(RecSysMapCreator.MAP_DELIMITER)[1]));
					index++;
				}
				if(i == 0)
					assertEquals(userIds, readOriginalIds);
				else if(i == 1)
					assertEquals(programIds, readOriginalIds);
				else
					assertEquals(eventIds, readOriginalIds);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	@After
	public void tearDown(){
		mapCreator.close();
		mapCreator = null;
	}
	
}
