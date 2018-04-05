package data.recsys.mapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Class that creates a mapping from the Recsys ids to original ids ranging from 0 to # of users/programs/event.
 * @author Jonathan Bergeron
 *
 */
public class RecSysMapCreator {
	
	/**
	 * The map delimiter used in the map file.
	 */
	public static final String MAP_DELIMITER = ",";
	
	/**
	 * The relative path to the resources folder.
	 */
	public static final String PATH_TO_RESOURCES = "src/main/resources/";
	
	/**
	 * The file name of the user ID map.
	 */
	private final String userIDToIDMapFileName;
	
	/**
	 * The file name of the program ID map.
	 */
	private final String programIDToIDMapFileName;
	
	/**
	 * The file name of the event ID map.
	 */
	private final String eventIDToIDMapFileName;

	/**
	 * Constructor of the RecSysMapCreator. It makes sure that the default name for the map file name is valid.
	 */
	public RecSysMapCreator() {
		userIDToIDMapFileName = getValidFileName("userIDToIDMap1") + ".txt";
		programIDToIDMapFileName = getValidFileName("programIDToIDMap1") + ".txt";
		eventIDToIDMapFileName = getValidFileName("eventIDToIDMap1") + ".txt";
	}
	
	/**
	 * Method that return all the file names of the maps.
	 * @return An array of string containing in order, the user, the program and the event map file name.
	 */
	public String[] getFileNames(){
		return new String[]{userIDToIDMapFileName,programIDToIDMapFileName,eventIDToIDMapFileName};
	}
	
	/**
	 * Getter Method that return the user ID map file name.
	 * @return A string representing the name of the user ID map file name.
	 */
	public String getuserIDToIDMapFileName() {
		return userIDToIDMapFileName;
	}
	
	/**
	 * Getter Method that return the user ID map path.
	 * @return A string representing the path of the user ID map file name.
	 */
	public String getUserIDToIDMapPath() {
		return PATH_TO_RESOURCES + userIDToIDMapFileName;
	}
	
	/**
	 * Getter Method that return the program ID map file name.
	 * @return A string representing the name of the program ID map file name.
	 */
	public String getProgramIDToIDMapFileName() {
		return programIDToIDMapFileName;
	}
	
	/**
	 * Getter Method that return the program ID map path.
	 * @return A string representing the name of the program ID map path.
	 */
	public String getProgramIDToIDMapPath() {
		return PATH_TO_RESOURCES + programIDToIDMapFileName;
	}
	
	/**
	 * Getter Method that return the event ID map file name.
	 * @return A string representing the name of the event ID map file name.
	 */
	public String getEventIDToIDMapFileName() {
		return eventIDToIDMapFileName;
	}
	
	/**
	 * Getter Method that return the event ID map path.
	 * @return A string representing the name of the event ID map path.
	 */
	public String getEventIDToIDMapPath() {
		return PATH_TO_RESOURCES + eventIDToIDMapFileName;
	}
	
	/**
	 * Method that creates the user ID map file.
	 * @param userIDs The list of distinct user ids in the recsys tv data set.
	 */
	public void createUserIDToIDMap(Set<Integer> userIDs) {
		createMap(userIDs, getUserIDToIDMapPath());
	}
	
	/**
	 * Method that creates the program ID map file.
	 * @param userIDs The list of distinct program ids in the recsys tv data set.
	 */
	public void createProgramIDToIDMap(Set<Integer> programIDs) {
		createMap(programIDs, getProgramIDToIDMapPath());
	}
	
	/**
	 * Method that creates the event ID map file.
	 * @param userIDs The list of distinct event ids in the recsys tv data set.
	 */
	public void createEventIDToIDMap(Set<Integer> eventIDs) {
		createMap(eventIDs, getEventIDToIDMapPath());
	}
	
	private void createMap(Set<Integer> ids, String path){
		BufferedWriter bw;
		try {
			
			bw = new BufferedWriter(new FileWriter(new File(path)));
			int mappedID = 0;
			for (Integer id : ids) {
				bw.write(id + MAP_DELIMITER + mappedID + "\n");
				mappedID++;
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
	
	/**
	 * Method that erases all the map created.
	 */
	public void close(){
		File userMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + userIDToIDMapFileName);
		File programMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + programIDToIDMapFileName);
		File eventMapFile = new File(RecSysMapCreator.PATH_TO_RESOURCES + eventIDToIDMapFileName);
		userMapFile.delete();
		programMapFile.delete();
		eventMapFile.delete();
	}
}
