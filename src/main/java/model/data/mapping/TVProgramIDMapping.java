package model.data.mapping;

import java.util.Set;

import model.data.TVProgram;

public class TVProgramIDMapping<P extends TVProgram> extends TVProgramMapping<P, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TVProgramIDMapping(Set<P> allValues) {
		super(allValues, TVProgram::programId);
	}

}
