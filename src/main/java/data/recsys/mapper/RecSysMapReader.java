package data.recsys.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that reads map created by the <class>RecSysMapCreator</class> and store them in HashMap.
 * @author Jonathan
 *
 */
public class RecSysMapReader {
	
	private Map<Integer, Integer> userIDToIdMap;
	private Map<Integer, Integer> programIDtoIDMap;
	private Map<Integer, Integer> eventIDtoIDMap;
	
	private String userIDToIDMapFileName;
	private String programIDToIDMapFileName;
	private String eventIDToIDMapFileName;

	/**
	 * Constructor of the RecSysMapReader class. It loads all the map from the given file names.
	 * @param fileNames An array of string containing the file names. It must be in this order; user, program and event file name.
	 */
	public RecSysMapReader(String[] fileNames) {
		this.userIDToIDMapFileName = fileNames[0];
		this.programIDToIDMapFileName = fileNames[1];
		this.eventIDToIDMapFileName = fileNames[2];
		initializeMaps(userIDToIDMapFileName, programIDToIDMapFileName,
				eventIDToIDMapFileName);
	}
	
	/**
	 * Getter method to get the user id map.
	 * @return The user id HashMap.
	 */
	public Map<Integer, Integer> getUserIDToIdMap(){
		return userIDToIdMap;
	}
	
	/**
	 * Getter method to get the program id map.
	 * @return The program id HashMap.
	 */
	public Map<Integer, Integer> getProgramIDtoIDMap(){
		return programIDtoIDMap;
	}
	
	/**
	 * Getter method to get the event id map.
	 * @return The event id HashMap.
	 */
	public Map<Integer, Integer> getEventIDtoIDMap(){
		return eventIDtoIDMap;
	}
	
	/**
	 * Getter method to get the user id map file name.
	 * @return The user id map file name.
	 */
	public String getUserIDMapFileName(){
		return userIDToIDMapFileName;
	}
	
	/**
	 * Getter method to get the program id map file name.
	 * @return The user id map file name.
	 */
	public String getProgramIDMapFileName(){
		return programIDToIDMapFileName;
	}
	
	/**
	 * Getter method to get the event id map file name.
	 * @return The user id map file name.
	 */
	public String getEventIDMapFileName(){
		return eventIDToIDMapFileName;
	}
	
	/**
	 * Method that maps a recsys user id to the mapped id.
	 * @param userID the recsys user id.
	 * @return the mapped id.
	 */
	public Integer mapUserIDtoID(int userID) {
		return userIDToIdMap.get(userID);
	}
	
	/**
	 * Method that maps a recsys program id to the mapped id.
	 * @param programID the recsys program id.
	 * @return the mapped id.
	 */
	public Integer mapProgramIDtoID(int programID) {
		return programIDtoIDMap.get(programID);
	}
	
	/**
	 * Method that maps a recsys event id to the mapped id.
	 * @param eventID the recsys event id.
	 * @return the mapped id.
	 */
	public Integer mapEventIDtoID(int eventID) {
		return eventIDtoIDMap.get(eventID);
	}
	
	/**
	 * Method that erases all the map.
	 */
	public void close(){
		File userMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + userIDToIDMapFileName);
		File programMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + programIDToIDMapFileName);
		File eventMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + eventIDToIDMapFileName);
		userMapFile.delete();
		programMapFile.delete();
		eventMapFile.delete();
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
}
