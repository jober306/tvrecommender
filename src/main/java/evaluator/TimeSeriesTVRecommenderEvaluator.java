package evaluator;

import java.util.List;

import data.EPG;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;
import evaluator.result.EvaluationResults;
import model.recommendation.AbstractRecommendation;
import util.TimeRange;

public class TimeSeriesTVRecommenderEvaluator<T extends TVProgram, U extends TVEvent, R extends AbstractRecommendation> {
	
	final TVDataSet<T, U> dataSet;
	final EPG<T> epg;
	
	public TimeSeriesTVRecommenderEvaluator(TVDataSet<T, U> dataSet, EPG<T> epg){
		this.dataSet = dataSet;
		this.epg = epg;
	}
	
	public List<EvaluationResults> evaluate(TimeRange trainingStartTimes, TimeRange trainingEndTimes, TimeRange testStartTimes, TimeRange testEndTimes){
		return null;
	}
	
}
