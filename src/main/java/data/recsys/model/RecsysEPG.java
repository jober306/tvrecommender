package data.recsys.model;

import org.apache.spark.api.java.JavaRDD;

import data.model.EPG;

public class RecsysEPG extends EPG<RecsysTVProgram>{

	public RecsysEPG(JavaRDD<RecsysTVProgram> electronicProgrammingGuide) {
		super(electronicProgrammingGuide);
	}

}
