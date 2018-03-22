package data;

import static util.TVDataSetUtilities.createSubDataSet;

import java.time.LocalDateTime;

/**
 * Class that represents a context in which a recommender lives.
 * @author Jonathan Bergeron
 *
 */
public class Context<T extends TVProgram, U extends TVEvent>{
	
	/**
	 * The electronic programming guide.
	 */
	final EPG<T> epg;
	
	/**
	 * The whole data set of events.
	 */
	final TVDataSet<T, U> events;
	
	/**
	 * The training set on which the recommender will train.
	 */
	final TVDataSet<T, U> trainingSet;
	
	
	public Context(EPG<T> epg, TVDataSet<T, U> events){
		this.epg = epg;
		this.events = events;
		this.trainingSet = events;
	}
	
	public Context(EPG<T> epg, TVDataSet<T, U> events, LocalDateTime trainingStartTime, LocalDateTime trainingEndTime){
		this.epg = epg;
		this.events = events;
		this.trainingSet = createSubDataSet(events, trainingStartTime, trainingEndTime);
	}
	
	public EPG<T> getEPG(){
		return epg;
	}
	
	public TVDataSet<T, U> getEvents(){
		return events;
	}

	public TVDataSet<T, U> getTrainingSet(){
		return trainingSet;
	}
}
