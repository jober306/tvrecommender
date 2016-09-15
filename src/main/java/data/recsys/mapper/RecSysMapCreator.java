package data.recsys.mapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import data.recsys.model.RecsysTVDataSet;

public class RecSysMapCreator {

	public static final String MAP_DELIMITER = ",";
	public static final String PATH_TO_RESOURCES = "src/main/resources/";

	private final String userIDToIDMapFileName;

	private final String programIDToIDMapFileName;

	private final String eventIDToIDMapFileName;

	public RecSysMapCreator() {
		userIDToIDMapFileName = getValidFileName("userIDToIDMap1") + ".txt";
		programIDToIDMapFileName = getValidFileName("programIDToIDMap1") + ".txt";
		eventIDToIDMapFileName = getValidFileName("eventIDToIDMap1") + ".txt";
	}

	public String getuserIDToIDMapFileName() {
		return userIDToIDMapFileName;
	}

	public String getUserIDToIDMapPath() {
		return PATH_TO_RESOURCES + userIDToIDMapFileName;
	}

	public String getProgramIDToIDMapFileName() {
		return programIDToIDMapFileName;
	}

	public String getProgramIDToIDMapPath() {
		return PATH_TO_RESOURCES + programIDToIDMapFileName;
	}

	public String getEventIDToIDMapFileName() {
		return eventIDToIDMapFileName;
	}

	public String getEventIDToIDMapPath() {
		return PATH_TO_RESOURCES + eventIDToIDMapFileName;
	}

	public void createUserIDToIDMap(List<Integer> userIDs) {
		BufferedWriter bw;
		try {
			
			bw = new BufferedWriter(new FileWriter(new File(getUserIDToIDMapPath())));
			int id = 0;
			for (Integer userID : userIDs) {
				bw.write(userID + MAP_DELIMITER + id + "\n");
				id++;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createProgramIDToIDMap(List<Integer> programIDs) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(
					getProgramIDToIDMapPath())));
			int id = 0;
			for (Integer programID : programIDs) {
				bw.write(programID + MAP_DELIMITER + id + "\n");
				id++;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createEventIDToIDMap(List<Integer> eventIDs) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(
					new FileWriter(new File(getEventIDToIDMapPath())));
			int id = 0;
			for (Integer eventID : eventIDs) {
				bw.write(eventID + MAP_DELIMITER + id + "\n");
				id++;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getValidFileName(String originalFileName){
		boolean validFilePath = false;
		String tempFilePath = originalFileName;
		int index = 1;
		while(!validFilePath){
			File f = new File(PATH_TO_RESOURCES + tempFilePath + ".txt");	
			if(f.exists()){
				int numCharToRemove = (int)Math.floor(Math.log10(index)) + 1;
				tempFilePath = tempFilePath.substring(0, tempFilePath.length() - numCharToRemove);
				index++;
				tempFilePath += index;
			}
			else{
				validFilePath = true;
			}
		}
		return tempFilePath;
	}
	
	public void close(){
		File userMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + userIDToIDMapFileName);
		File programMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + programIDToIDMapFileName);
		File eventMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + eventIDToIDMapFileName);
		userMapFile.delete();
		programMapFile.delete();
		eventMapFile.delete();
	}
}
