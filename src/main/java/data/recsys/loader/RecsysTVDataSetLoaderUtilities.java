package data.recsys.loader;

import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;

import data.recsys.model.RecsysTVEvent;

public class RecsysTVDataSetLoaderUtilities implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Method that map all lines in a RDD to a RDD of
	 * <class>RecsysTVEvent</class>.
	 * 
	 * @param lines
	 *            A JavaRDD of <class>String</class> containing the recsys tv
	 *            event in csv format.
	 * @return A JavaRDD of <class>RecsysTVEvent</class>.
	 */
	public static JavaRDD<RecsysTVEvent> mapLinesToTVEvent(JavaRDD<String> lines) {
		return lines.map(line -> RecsysTVDataSetLoaderUtilities
				.mapLineToTVEvent(line));
	}

	/**
	 * Method that map a single line to a Recsys tv event.
	 * 
	 * @param line
	 *            The String representing the Recsys tv event in csv format.
	 * @return The <class> RecsysTVEvent<\class> object representing the line.
	 */
	public static RecsysTVEvent mapLineToTVEvent(String line) {
		String[] row = line.split(",");
		return new RecsysTVEvent(Short.parseShort(row[0]),
				Short.parseShort(row[1]), Byte.parseByte(row[2]),
				Byte.parseByte(row[3]), Byte.parseByte(row[4]),
				Integer.parseInt(row[5]), Integer.parseInt(row[6]),
				Integer.parseInt(row[7]), Integer.parseInt(row[8]));
	}
}
