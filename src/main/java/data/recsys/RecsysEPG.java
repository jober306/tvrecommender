package data.recsys;

import org.apache.spark.api.java.JavaRDD;

import data.EPG;

/**
 * Class that represents the epg of the recsys tv data set.
 * @author Jonathan Bergeron
 *
 */
public class RecsysEPG extends EPG<RecsysTVProgram> {

	/**
	 * Constructor of the recsys epg that simply encapsulate the epg data. 
	 * @param electronicProgrammingGuide The epg data.
	 * @param sc The java spark context used to load the epg data.
	 */
	public RecsysEPG(JavaRDD<RecsysTVProgram> electronicProgrammingGuide) {
		super(electronicProgrammingGuide);
	}

}
