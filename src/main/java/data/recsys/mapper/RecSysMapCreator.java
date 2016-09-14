package data.recsys.mapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import data.recsys.model.RecsysTVDataSet;

public class RecSysMapCreator {

	public static final String mapDelimiter = ",";

	private final String userIDToIDMapFileName;
	private final String userIDToIDMapPath;

	private final String programIDToIDMapFileName;
	private final String programIDToIDMapPath;

	private final String eventIDToIDMapFileName;
	private final String eventIDToIDMapPath;

	public RecSysMapCreator() {
		userIDToIDMapFileName = "userIDToIDMap.txt";
		programIDToIDMapFileName = "programIDToIDMap.txt";
		eventIDToIDMapFileName = "eventIDToIDMap.txt";
		userIDToIDMapPath = "src/main/resources/" + userIDToIDMapFileName;
		programIDToIDMapPath = "src/main/resources/" + programIDToIDMapFileName;
		eventIDToIDMapPath = "src/main/resources/" + eventIDToIDMapFileName;

	}

	public String getuserIDToIDMapFileName() {
		return userIDToIDMapFileName;
	}

	public String getUserIDToIDMapPath() {
		return userIDToIDMapPath;
	}

	public String getProgramIDToIDMapFileName() {
		return programIDToIDMapFileName;
	}

	public String getProgramIDToIDMapPath() {
		return programIDToIDMapPath;
	}

	public String getEventIDToIDMapFileName() {
		return eventIDToIDMapFileName;
	}

	public String getEventIDToIDMapPath() {
		return eventIDToIDMapPath;
	}

	public void createUserIDToIDMap(RecsysTVDataSet dataSet) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(userIDToIDMapPath)));
			List<Integer> userIDs = dataSet.getAllUserIds();
			int id = 0;
			for (Integer userID : userIDs) {
				bw.write(userID + mapDelimiter + id + "\n");
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
					programIDToIDMapPath)));
			List<Integer> programIDs = dataSet.getAllProgramIds();
			int id = 0;
			for (Integer programID : programIDs) {
				bw.write(programID + mapDelimiter + id + "\n");
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
					new FileWriter(new File(eventIDToIDMapPath)));
			List<Integer> programIDs = dataSet.getAllProgramIds();
			int id = 0;
			for (Integer programID : programIDs) {
				bw.write(programID + mapDelimiter + id + "\n");
				id++;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
