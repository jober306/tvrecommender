package data.model;

import static time.utilities.TimeUtilities.isBetweenInclusive;

import org.apache.spark.api.java.JavaRDD;

public abstract class EDG<T extends TVProgram> {
	
	protected JavaRDD<T> electronicProgrammingGuide;
	
	public JavaRDD<T> getCorrespondingPrograms(TVEvent event){
		JavaRDD<T> programsInTimeWindow = electronicProgrammingGuide.filter(program -> isBetweenInclusive(program.getStartTime(), program.getEndTime(), event.getWatchTime()));
		return programsInTimeWindow.filter(program -> event.getChannelId() == program.getChannelId() && program.getProgramId() == event.getChannelId());
	}
}
