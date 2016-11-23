package mlllib.evaluator;

import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import data.model.TVDataSet;
import data.model.TVEvent;
import data.utility.TVDataSetUtilities;
import mllib.recommender.SpaceAlignmentRecommender;
import mllib.recommender.collaborativefiltering.ItemBasedRecommender;

public class SpaceAlignmentEvaluator <T extends TVEvent>{
	
	TVDataSet<T> trainingSet;
	TVDataSet<T> testSet;
	SpaceAlignmentRecommender<T> actualRecommender;
	ItemBasedRecommender<T> expectedRecommender;
	
	EvaluationMeasure[] measures;
	Map<EvaluationMeasure, Double> evaluationResults;
	
	public SpaceAlignmentEvaluator(TVDataSet<T> tvDataSet, EvaluationMeasure[] measures) throws NoSuchMethodException, SecurityException{
		JavaSparkContext sc = tvDataSet.getJavaSparkContext();
		TVDataSetUtilities<T> dataSetUtilities = new TVDataSetUtilities<T>();
		int week = 3;
		int r = 4;
		JavaRDD<T> fullDataSet = tvDataSet.getEventsData();
		JavaRDD<T> week3 = dataSetUtilities.filterByIntervalOfWeek(fullDataSet, week, week);
		JavaRDD<T> weekFourDayOne = dataSetUtilities.filterByIntervalOfDay(dataSetUtilities.filterByIntervalOfWeek(fullDataSet, week+1, week+1),1,1);
		trainingSet = tvDataSet.buildDataSetFromRawData(week3, sc);
		testSet = tvDataSet.buildDataSetFromRawData(week3.union(weekFourDayOne), sc);
		actualRecommender = new SpaceAlignmentRecommender<T>(trainingSet, r);
		expectedRecommender = new ItemBasedRecommender<T>(testSet);
		this.measures = measures;
	}
	
	public void evaluate(){
		for(EvaluationMeasure measure : measures){
			switch (measure) {
			case NEIGHBOURHOOD_COVERAGE:
				evaluateNeighbourhoodCoverage();
				break;
			case MEAN_AVERAGE_PRECISION:
				evaluateMeanAveragePrecision();
				break;
			case MEAN_AVERAGE_RECALL:
				evaluateMeanAverageRecall();
				break;
			default:
				break;
			}
		}
	}
	
	public Map<EvaluationMeasure, Double> getResults(){
		return evaluationResults;
	}
	
	private void evaluateNeighbourhoodCoverage(){
		
	}
	
	private void evaluateMeanAveragePrecision(){
		
	}
	
	private void evaluateMeanAverageRecall(){
		
	}
}
