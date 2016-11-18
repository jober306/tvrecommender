package mlllib.evaluator;

import java.util.Map;

import data.model.TVDataSet;
import data.model.TVEvent;

public class SpaceAlignmentEvaluator <T extends TVEvent>{
	
	TVDataSet<T> dataSet;
	EvaluationMeasure[] measures;
	
	Map<EvaluationMeasure, Double> evaluationResults;
	
	public SpaceAlignmentEvaluator(TVDataSet<T> dataSet, EvaluationMeasure[] measures){
		this.dataSet = dataSet;
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
