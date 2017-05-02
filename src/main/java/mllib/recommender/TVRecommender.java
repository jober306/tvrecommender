package mllib.recommender;

import static data.utility.TVDataSetUtilities.filterByDateTime;

import java.time.LocalDateTime;
import java.util.List;

import data.model.EPG;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

public abstract class TVRecommender<T extends TVProgram, U extends TVEvent> {

	protected EPG<T> epg;
	protected TVDataSet<U> dataSet;
	protected TVDataSet<U> trainingSet;

	public TVRecommender(EPG<T> epg, TVDataSet<U> dataSet) {
		this.epg = epg;
		this.dataSet = dataSet;
		this.trainingSet = dataSet;
	}

	public TVRecommender(EPG<T> epg, TVDataSet<U> dataSet,
			LocalDateTime trainingStartTime, LocalDateTime trainingEndTime) {
		this(epg, dataSet);
		createTrainingSet(trainingStartTime, trainingEndTime);
	}

	/**
	 * Method that returns the data set that was used to train this recommender.
	 * 
	 * @return The training tv data set.
	 */
	public TVDataSet<U> getTrainingSet() {
		return trainingSet;
	}

	public void createTrainingSet(LocalDateTime startTime, LocalDateTime endTime) {
		this.trainingSet = dataSet.buildDataSetFromRawData(
				filterByDateTime(dataSet.getEventsData(), startTime, endTime),
				dataSet.getJavaSparkContext());
	}

	abstract public List<Integer> recommend(int userId,
			LocalDateTime targetTime, int numberOfResults);

	abstract public List<Integer> recommend(int userId,
			LocalDateTime startTargetTime, LocalDateTime endTargetTime,
			int numberOfResults);
}
