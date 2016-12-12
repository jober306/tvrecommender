package data.model;

import java.time.LocalTime;
import java.util.Map;

public abstract class EDG<T extends TVProgram> {
	
	protected Map<LocalTime, T> electronicProgrammingGuide;
}
