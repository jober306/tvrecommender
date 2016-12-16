package data.recsys.model;

import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;

import data.model.EPG;

public class RecsysEPG extends EPG<RecsysTVProgram> implements Serializable{

	private static final long serialVersionUID = 1L;

	public RecsysEPG(JavaRDD<RecsysTVProgram> electronicProgrammingGuide) {
		super(electronicProgrammingGuide);
	}

}
