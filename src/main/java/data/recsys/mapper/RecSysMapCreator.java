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
		userIDToIDMapFileName = getValidFileName("userIDToIDMap") + ".txt";
		programIDToIDMapFileName = getValidFileName("programIDToIDMap") + ".txt";
		eventIDToIDMapFileName = getValidFileName("eventIDToIDMap") + ".txt";
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

	public void createUserIDToIDMap(RecsysTVDataSet dataSet) {
		BufferedWriter bw;
		try {
			
			bw = new BufferedWriter(new FileWriter(new File(getUserIDToIDMapPath())));
			List<Integer> userIDs = dataSet.getAllUserIds();
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

	public void createProgramIDToIDMap(RecsysTVDataSet dataSet) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(
					getProgramIDToIDMapPath())));
			List<Integer> programIDs = dataSet.getAllProgramIds();
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

	public void createEventIDToIDMap(RecsysTVDataSet dataSet) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(
					new FileWriter(new File(getEventIDToIDMapPath())));
			List<Integer> programIDs = dataSet.getAllProgramIds();
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
	
	private String getValidFileName(String originalFileName){
		boolean validFilePath = false;
		String tempFilePath = originalFileName;
		int index = 1;
		while(!validFilePath){
			File f = new File(PATH_TO_RESOURCES + tempFilePath + ".txt");	
			if(f.exists()){
				int numCharToRemove = (int)Math.floor(Math.log10(index));
				tempFilePath = tempFilePath.substring(tempFilePath.length() - numCharToRemove, tempFilePath.length());
				index++;
				tempFilePath += index;
			}
			else{
				validFilePath = true;
			}
		}
		return tempFilePath;
	}
	
	public void createMaps(RecsysTVDataSet dataSet){
		createUserIDToIDMap(dataSet);
		createProgramIDToIDMap(dataSet);
		createEventIDToIDMap(dataSet);
	}
}
