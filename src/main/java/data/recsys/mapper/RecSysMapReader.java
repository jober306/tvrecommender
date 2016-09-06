package data.recsys.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import data.recsys.utility.RecsysTVDataSetUtilities;

public class RecSysMapReader {
	
	
	static Map<Integer,Integer> userIDToIdMap;
	static Map<Integer, Integer> programIDtoIDMap;
	
	static{
		userIDToIdMap = new HashMap<Integer,Integer>();
		try {
			InputStream userIDToIdStream = RecsysTVDataSetUtilities.class.getResourceAsStream("\\" + RecSysMapCreator.userIDToIDMapFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(userIDToIdStream));
			String line = "";
			while((line = br.readLine())!= null){
				String[] lineInfos = line.split(RecSysMapCreator.mapDelimiter);
				Integer userID = Integer.parseInt(lineInfos[0]);
				Integer  ID = Integer.parseInt(lineInfos[1]);
				userIDToIdMap.put(userID, ID);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		programIDtoIDMap = new HashMap<Integer,Integer>();
		try {
			InputStream programIDToIdStream = RecsysTVDataSetUtilities.class.getResourceAsStream("\\" + RecSysMapCreator.programIDToIDMapFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(programIDToIdStream));
			String line = "";
			while((line = br.readLine())!= null){
				String[] lineInfos = line.split(RecSysMapCreator.mapDelimiter);
				Integer programID = Integer.parseInt(lineInfos[0]);
				Integer  ID = Integer.parseInt(lineInfos[1]);
				programIDtoIDMap.put(programID, ID);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Integer mapUserIDtoID(int userID){
		return userIDToIdMap.get(userID);
	}
	
	public static Integer mapProgramIDtoID(int programID){
		return programIDtoIDMap.get(programID);
	}
}
