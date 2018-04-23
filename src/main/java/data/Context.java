package data;

import static util.TVDataSetUtilities.createSubDataSet;

import java.time.LocalDateTime;

/**
 * Class that represents a context in which a recommender lives.
 * @author Jonathan Bergeron
 *
 */
public class Context<T extends TVProgram, U extends AbstractTVEvent<T>>{
	
	/**
	 * The electronic programming guide.
	 */
	final EPG<T> epg;
	
	/**
	 * The whole data set of events.
	 */
	final TVDataSet<T, U> events;
	
	
	final LocalDateTime trainingStartTime;
	final LocalDateTime trainingEndTime;
	/**
	 * The training subset on which the recommender will be trained.
	 */
	final TVDataSet<T, U> trainingSet;
	
	/**
	 * Constructor of the class, the training time are set to be the whole tv dataset.
	 * @param epg The electronic programming guide.
	 * @param events The tv events that occurred in the epg. 
	 */
	public Context(EPG<T> epg, TVDataSet<T, U> events){
		this.epg = epg;
		this.events = events;
		this.trainingStartTime = events.startTime();
		this.trainingEndTime = events.endTime();
		this.trainingSet = events;
	}
	
	/**
	 * Constructor of the class.
	 * @param epg The electronic programming guide.
	 * @param events The tv events that occurred in the epg. 
	 * @param trainingStartTime The training start time.
	 * @param trainingEndTime The training end time.
	 */
	public Context(EPG<T> epg, TVDataSet<T, U> events, LocalDateTime trainingStartTime, LocalDateTime trainingEndTime){
		this.epg = epg;
		this.events = events;
		this.trainingStartTime = trainingStartTime;
		this.trainingEndTime = trainingEndTime;
		this.trainingSet = createSubDataSet(events, trainingStartTime, trainingEndTime);
	}
	
	/**
	 * Method that returns the epg.
	 * @return The epg.
	 */
	public EPG<T> getEPG(){
		return epg;
	}
	
	/**
	 * Method that returns the whole tv events dataset.
	 * @return The tv events dataset.
	 */
	public TVDataSet<T, U> getTvDataSet(){
		return events;
	}
	
	/**
	 * Method that returns the training dataset.
	 * @return The tv dataset used to train.
	 */
	public TVDataSet<T, U> getTrainingSet(){
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
	
	/**
	 * Method that closes all resources held by this context.
	 */
	public void close(){
		events.close();
		trainingSet.close();
	}
}
