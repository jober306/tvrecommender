package data.recsys.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import data.recsys.utility.RecsysTVDataSetUtilities;

public class RecSysMapReader {

	private Map<Integer, Integer> userIDToIdMap;
	private Map<Integer, Integer> programIDtoIDMap;
	private Map<Integer, Integer> eventIDtoIDMap;

	public RecSysMapReader(String userIDToIDMapFileName,
			String programIDToIDMapFileName, String eventIDToIDMapFileName) {
		initializeMaps(userIDToIDMapFileName, programIDToIDMapFileName,
				eventIDToIDMapFileName);
	}

	private void initializeMaps(String userIDToIDMapFileName,
			String programIDToIDMapFileName, String eventIDToIDMapFileName) {
		userIDToIdMap = new HashMap<Integer, Integer>();
		try {
			InputStream userIDToIdStream = RecsysTVDataSetUtilities.class
					.getResourceAsStream("\\" + userIDToIDMapFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					userIDToIdStream));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineInfos = line.split(RecSysMapCreator.mapDelimiter);
				Integer userID = Integer.parseInt(lineInfos[0]);
				Integer ID = Integer.parseInt(lineInfos[1]);
				userIDToIdMap.put(userID, ID);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		programIDtoIDMap = new HashMap<Integer, Integer>();
		try {
			InputStream programIDToIdStream = RecsysTVDataSetUtilities.class
					.getResourceAsStream("\\" + programIDToIDMapFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					programIDToIdStream));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineInfos = line.split(RecSysMapCreator.mapDelimiter);
				Integer programID = Integer.parseInt(lineInfos[0]);
				Integer ID = Integer.parseInt(lineInfos[1]);
				programIDtoIDMap.put(programID, ID);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		eventIDtoIDMap = new HashMap<Integer, Integer>();
		try {
			InputStream eventIDToIdStream = RecsysTVDataSetUtilities.class
					.getResourceAsStream("\\" + eventIDToIDMapFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					eventIDToIdStream));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineInfos = line.split(RecSysMapCreator.mapDelimiter);
				Integer eventID = Integer.parseInt(lineInfos[0]);
				Integer ID = Integer.parseInt(lineInfos[1]);
				programIDtoIDMap.put(eventID, ID);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Integer mapUserIDtoID(int userID) {
		return userIDToIdMap.get(userID);
	}

	public Integer mapProgramIDtoID(int programID) {
		return programIDtoIDMap.get(programID);
	}

	public Integer mapEventIDtoID(int eventID) {
		return eventIDtoIDMap.get(eventID);
	}
}
