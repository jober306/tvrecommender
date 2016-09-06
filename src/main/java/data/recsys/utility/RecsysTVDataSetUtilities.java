package data.recsys.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.recsys.model.RecsysTVEvent;

public class RecsysTVDataSetUtilities {
	
	public static final String GENRE_SUBGENRE_MAPPING_PATH = "/tv-audience-dataset/genreSubgenreMapping.txt";
	
	public static Map<Byte, String> genreToNameMap;
	public static Map<Byte, Map<Byte,String>> subgenreToNameMap;
	static{
		genreToNameMap = new HashMap<Byte,String>();
		subgenreToNameMap = new HashMap<Byte, Map<Byte,String>>();
		try {
			InputStream genreSubgenreStream = RecsysTVDataSetUtilities.class.getResourceAsStream(GENRE_SUBGENRE_MAPPING_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(genreSubgenreStream));
			String line = "";
			while((line = br.readLine())!= null){
				String[] lineInfos = line.split(" ");
				String genre = lineInfos[0];
				Byte genreID = Byte.parseByte(lineInfos[1]);
				String subgenre = lineInfos[2];
				Byte subgenreID = Byte.parseByte(lineInfos[3]);
				genreToNameMap.put(genreID, genre);
				if(!subgenreToNameMap.containsKey(genreID)){
					subgenreToNameMap.put(genreID, new HashMap<Byte,String>());
				}
				subgenreToNameMap.get(genreID).put(subgenreID, subgenre);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getGenreName(byte genreID){
		return genreToNameMap.get(genreID);
	}
	
	public static String getSubgenreName(byte genreID, byte subgenreID){
		return subgenreToNameMap.get(genreID).get(subgenreID);
	}
	
	public static boolean isGenreSubgenreMapNotEmpty(){
		return !genreToNameMap.isEmpty() && !subgenreToNameMap.isEmpty();
	}
}
