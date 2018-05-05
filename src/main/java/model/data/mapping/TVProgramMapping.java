package model.data.mapping;

import java.util.Set;

import model.data.TVProgram;
import util.function.SerializableFunction;

public class TVProgramMapping<P extends TVProgram, PM> extends AbstractMapping<P, PM> {

	private static final long serialVersionUID = 1L;

	public TVProgramMapping(Set<P> allValues, SerializableFunction<? super P, ? extends PM> valueMapper) {
		super(allValues, valueMapper);
	}
}
