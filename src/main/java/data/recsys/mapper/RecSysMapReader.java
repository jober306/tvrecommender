package data.recsys.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	private String userIDToIDMapFileName;
	private String programIDToIDMapFileName;
	private String eventIDToIDMapFileName;

	public RecSysMapReader(String[] fileNames) {
		this.userIDToIDMapFileName = fileNames[0];
		this.programIDToIDMapFileName = fileNames[1];
		this.eventIDToIDMapFileName = fileNames[2];
		initializeMaps(userIDToIDMapFileName, programIDToIDMapFileName,
				eventIDToIDMapFileName);
	}
	
	public String getUserIDMapFileName(){
		return userIDToIDMapFileName;
	}
	
	public String getProgramIDMapFileName(){
		return programIDToIDMapFileName;
	}
	
	public String getEventIDMapFileName(){
		return eventIDToIDMapFileName;
	}

	private void initializeMaps(String userIDToIDMapFileName,
			String programIDToIDMapFileName, String eventIDToIDMapFileName) {
		userIDToIdMap = new HashMap<Integer, Integer>();
		programIDtoIDMap = new HashMap<Integer, Integer>();
		eventIDtoIDMap = new HashMap<Integer, Integer>();
		loadMapData(userIDToIdMap, RecSysMapCreator.PATH_TO_RESOURCES + userIDToIDMapFileName);
		loadMapData(programIDtoIDMap, RecSysMapCreator.PATH_TO_RESOURCES + programIDToIDMapFileName);
		loadMapData(eventIDtoIDMap, RecSysMapCreator.PATH_TO_RESOURCES + eventIDToIDMapFileName);
	}
	
	private void loadMapData(Map<Integer, Integer> map, String mapFilePath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(mapFilePath)));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineInfos = line.split(RecSysMapCreator.MAP_DELIMITER);
				Integer originalID = Integer.parseInt(lineInfos[0]);
				Integer ID = Integer.parseInt(lineInfos[1]);
				map.put(originalID, ID);
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
	
	public void close(){
		File userMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + userIDToIDMapFileName);
		File programMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + programIDToIDMapFileName);
		File eventMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + eventIDToIDMapFileName);
		userMapFile.delete();
		programMapFile.delete();
		eventMapFile.delete();
	}
}
