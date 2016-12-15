package mllib.recommender;

import java.time.LocalDateTime;
import java.util.List;

import data.model.EPG;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

public abstract class TVRecommender<T extends TVProgram, U extends TVEvent> {
	
	protected EPG<T> epg;
	protected TVDataSet<U> dataSet;
	
	public TVRecommender(EPG<T> epg, TVDataSet<U> dataSet){
		this.epg = epg;
		this.dataSet = dataSet;
	}

	abstract public List<Integer> recommend(int userId, LocalDateTime time, int numberOfResults);
}
