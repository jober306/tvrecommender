package data;

import java.time.LocalDateTime;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

/**
 * Class that represents a context in which a recommender lives.
 * @author Jonathan Bergeron
 *
 */
public class Context<U extends User, P extends TVProgram, E extends TVEvent<U, P>>{
	
	/**
	 * The electronic programming guide.
	 */
	final EPG<P> epg;
	
	/**
	 * The whole data set of events.
	 */
	final TVDataSet<U, P, E> events;
	
	
	final LocalDateTime trainingStartTime;
	final LocalDateTime trainingEndTime;
	/**
	 * The training subset on which the recommender will be trained.
	 */
	final TVDataSet<U, P, E> trainingSet;
	
	/**
	 * Constructor of the class, the training time are set to be the whole tv dataset.
	 * @param epg The electronic programming guide.
	 * @param dataSet The tv events that occurred in the epg. 
	 */
	public Context(EPG<P> epg, TVDataSet<U, P, E> dataSet){
		this.epg = epg;
		this.events = dataSet;
		this.trainingStartTime = dataSet.startTime();
		this.trainingEndTime = dataSet.endTime();
		this.trainingSet = dataSet;
	}
	
	/**
	 * Constructor of the class.
	 * @param epg The electronic programming guide.
	 * @param dataSet The tv events that occurred in the epg. 
	 * @param trainingStartTime The training start time.
	 * @param trainingEndTime The training end time.
	 */
	public Context(EPG<P> epg, TVDataSet<U, P, E> dataSet, LocalDateTime trainingStartTime, LocalDateTime trainingEndTime){
		this.epg = epg;
		this.events = dataSet;
		this.trainingStartTime = trainingStartTime;
		this.trainingEndTime = trainingEndTime;
		this.trainingSet = dataSet.filterByDateTime(trainingStartTime, trainingEndTime);
	}
	
	/**
	 * Method that returns the epg.
	 * @return The epg.
	 */
	public EPG<P> getEPG(){
		return epg;
	}
	
	/**
	 * Method that returns the whole tv events dataset.
	 * @return The tv events dataset.
	 */
	public TVDataSet<U, P, E> getTvDataSet(){
		return events;
	}
	
	/**
	 * Method that returns the training dataset.
	 * @return The tv dataset used to train.
	 */
	public TVDataSet<U, P, E> getTrainingSet(){
		return trainingSet;
	}

	/**
	 * Method that return the training start time.
	 * If none was specified it returns the the earliest event time.
	 * @return The training start time.
	 */
	public LocalDateTime getTrainingStartTime() {
		return trainingStartTime;
	}

	/**
	 * Method that return the training end time.
	 * If none was specified it returns the the latest event time.
	 * @return The training end time.
	 */
	public LocalDateTime getTrainingEndTime() {
		return trainingEndTime;
	}
}
