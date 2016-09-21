package data.recsys.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that provides some utilities methods related to the recsys tv data set.
 * @author Jonathan Bergeron
 *
 */
public class RecsysTVDataSetUtilities {
	
	static final String GENRE_SUBGENRE_MAPPING_PATH = "/tv-audience-dataset/genreSubgenreMapping.txt";
	
	static Map<Byte, String> genreToNameMap;
	static Map<Byte, Map<Byte,String>> subgenreToNameMap;
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
	
	/**
	 * Getter method that maps a genre id to the genre it represents.
	 * @param genreID The genre id.
	 * @return The genre it represents.
	 */
	public static String getGenreName(byte genreID){
		return genreToNameMap.get(genreID);
	}
	
	/**
	 * Getter method that maps a subgenre id to the subgenre it represents.
	 * @param subgenreID The subgenre id.
	 * @return The subgenre it represents.
	 */
	public static String getSubgenreName(byte genreID, byte subgenreID){
		return subgenreToNameMap.get(genreID).get(subgenreID);
	}
	
	/**
	 * Method that check if both map are not empty.
	 * @return True if both map are not empty, false otherwise.
	 */
	public static boolean isGenreSubgenreMapNotEmpty(){
		return !genreToNameMap.isEmpty() && !subgenreToNameMap.isEmpty();
	}
}
