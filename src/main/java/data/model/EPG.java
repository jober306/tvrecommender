package data.model;

import static time.utilities.TimeUtilities.isDateTimeBetween;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;

public abstract class EPG<T extends TVProgram> {
	
	protected JavaRDD<T> electronicProgrammingGuide;
	
	public EPG(JavaRDD<T> electronicProgrammingGuide){
		this.electronicProgrammingGuide = electronicProgrammingGuide;
	}
	
	public JavaRDD<T> getEPG(){
		return electronicProgrammingGuide;
	}
	
	public JavaRDD<T> getJavaRDdProgramsAtWatchTime(LocalDateTime targetWatchTime){
		return electronicProgrammingGuide.filter(program -> isDateTimeBetween(program.getStartTime(), program.getEndTime(), targetWatchTime));
	}
	
	public List<T> getListProgramsAtWatchTime(LocalDateTime targetWatchTime){
		return electronicProgrammingGuide.filter(program -> isDateTimeBetween(program.getStartTime(), program.getEndTime(), targetWatchTime)).collect();
	}
	
	public JavaRDD<T> getCorrespondingPrograms(TVEvent event){
		JavaRDD<T> programsInTimeWindow = electronicProgrammingGuide.filter(program -> isDateTimeBetween(program.getStartTime(), program.getEndTime(), event.getWatchTime()));
		return programsInTimeWindow.filter(program -> event.getChannelId() == program.getChannelId() && program.getProgramId() == event.getChannelId());
	}
}
