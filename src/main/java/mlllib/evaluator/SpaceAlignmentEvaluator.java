package mlllib.evaluator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;

import com.google.inject.spi.Element;

import data.model.TVDataSet;
import data.model.TVEvent;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.mapper.MappedIds;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import data.utility.TVDataSetUtilities;
import mllib.recommender.SpaceAlignmentRecommender;
import mllib.recommender.collaborativefiltering.ItemBasedRecommender;
import scala.Tuple2;

public class SpaceAlignmentEvaluator <T extends TVEvent>{
	
	TVDataSet<T> trainingSet;
	TVDataSet<T> testSet;
	SpaceAlignmentRecommender<T> actualRecommender;
	ItemBasedRecommender<T> expectedRecommender;
	
	List<Tuple2<Integer, Vector>> originalsNewItemsIds;
	boolean trainingSetIdsMapped;
	boolean testSetIdsMapped;
	MappedIds trainingSetMap;
	MappedIds testSetMap;
	
	EvaluationMeasure[] measures;
	Map<EvaluationMeasure, Double> evaluationResults;
	
	public SpaceAlignmentEvaluator(TVDataSet<T> tvDataSet, EvaluationMeasure[] measures){
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
		originalsNewItemsIds = weekFourDayOne.mapToPair(tvEvent -> new Tuple2<Integer, Vector>(tvEvent.getProgramID(), tvEvent.getProgramFeatureVector())).reduceByKey((arg1, arg2) -> arg1).collect();
		initializeMap();
		this.measures = measures;
	}
	
	private void initializeMap(){
		trainingSetIdsMapped = trainingSet instanceof MappedIds;
		testSetIdsMapped = testSet instanceof MappedIds;
		trainingSetMap = trainingSetIdsMapped ? (MappedIds) trainingSet : null;
		testSetMap = testSetIdsMapped ? (MappedIds) testSet : null;
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
		int n = 10;
		double totalCoverage = 0.0d;
		for(Tuple2<Integer,Vector> originalItemIdsContent : originalsNewItemsIds){
			int originalNewItemId = originalItemIdsContent._1();
			Vector newItemContent  = originalItemIdsContent._2();
			int mappedExpectedItemId = testSetMap == null ? originalNewItemId : testSetMap.getMappedProgramID(originalNewItemId);
			List<Integer> actualNeighboursMappedID = getFirstArgument(actualRecommender.predictNewItemNeighbourhood(newItemContent, n));
			List<Integer> expectedNeighboursMappedID = getFirstArgument(expectedRecommender.predictItemNeighbourhood(mappedExpectedItemId, n));
			expectedNeighboursMappedID = substract(expectedNeighboursMappedID, getFirstArgument(originalsNewItemsIds));
			List<Integer> actualNeighboursOriginalID = trainingSetIdsMapped ? getOriginalItemIds(trainingSetMap, actualNeighboursMappedID) : actualNeighboursMappedID;
			List<Integer> expectedNeighboursOriginalID = testSetIdsMapped ? getOriginalItemIds(testSetMap, expectedNeighboursMappedID) : expectedNeighboursMappedID;
			totalCoverage += (double)intersection(actualNeighboursOriginalID, expectedNeighboursOriginalID).size() / (double)expectedNeighboursOriginalID.size();
		}
		evaluationResults.put(EvaluationMeasure.NEIGHBOURHOOD_COVERAGE, totalCoverage / (double) originalsNewItemsIds.size());
	}
	
	private <E,U> List<E> getFirstArgument(List<Tuple2<E, U>> neighbourIndexesAndValues){
		return neighbourIndexesAndValues.stream().map(Tuple2::_1).collect(Collectors.toList());
	} 
	
	private <E,U> List<U> getSecondArgument(List<Tuple2<E, U>> neighbourIndexesAndValues){
		return neighbourIndexesAndValues.stream().map(Tuple2::_2).collect(Collectors.toList());
	}
	
	private <U> List<U> intersection(List<U> l1, List<U> l2){
		return l1.stream().filter(l2::contains).collect(Collectors.toList());
	}
	
	private <U> List<U> substract(List<U> original, List<U> listToSubstract){
		return original.stream().filter(element -> !listToSubstract.contains(element)).collect(Collectors.toList());
	}
	
	private List<Integer> getOriginalItemIds(MappedIds map, List<Integer> l){
		return l.stream().map(id -> map.getOriginalProgramID(id)).collect(Collectors.toList());
	}
	
	private void evaluateMeanAveragePrecision(){
		
	}
	
	private void evaluateMeanAverageRecall(){
		
	}
	
	public static void main(String[] args){
		EvaluationMeasure[] measures = new EvaluationMeasure[]{EvaluationMeasure.NEIGHBOURHOOD_COVERAGE};
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataSet = loader.loadDataSet();
		SpaceAlignmentEvaluator<RecsysTVEvent> evaluator = new SpaceAlignmentEvaluator<RecsysTVEvent>(dataSet, measures);
		evaluator.evaluate();
		System.out.println("RESULT: " + evaluator.getResults().get(EvaluationMeasure.NEIGHBOURHOOD_COVERAGE));
	}
}
