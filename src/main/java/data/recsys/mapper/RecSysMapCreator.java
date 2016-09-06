package data.recsys.mapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;

public class RecSysMapCreator {
	
	public static final String mapDelimiter = ",";
	
	public final static String userIDToIDMapFileName = "userIDToIDMap.txt";
	private final static String userIDToIDMapPath = "src/main/resources/" + userIDToIDMapFileName;
	
	public final static String programIDToIDMapFileName = "programIDToIDMap.txt";
	private final static String programIDToIDMapPath = "src/main/resources/" + programIDToIDMapFileName;
	
	public RecSysMapCreator(){
		
	}
	
	
	public static void createUserIDToIDMap(RecsysTVDataSet dataSet){
		BufferedWriter bw;
		try {
		bw = new BufferedWriter(new FileWriter(new File(userIDToIDMapPath)));
		List<Integer> userIDs = dataSet.getAllUserIds();
		int id = 0;
		for(Integer userID : userIDs){
			bw.write(userID + mapDelimiter + id + "\n");
			id++;
		}
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createProgramIDToIDMap(RecsysTVDataSet dataSet){
		BufferedWriter bw;
		try {
		bw = new BufferedWriter(new FileWriter(new File(programIDToIDMapPath)));
		List<Integer> programIDs = dataSet.getAllProgramIds();
		int id = 0;
		for(Integer programID : programIDs){
			bw.write(programID + mapDelimiter + id + "\n");
			id++;
		}
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		RecsysTVDataSetLoader dataSetLoader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataSet = dataSetLoader.loadDataSet();
		RecSysMapCreator.createProgramIDToIDMap(dataSet);
		RecSysMapCreator.createUserIDToIDMap(dataSet);
	}
}
