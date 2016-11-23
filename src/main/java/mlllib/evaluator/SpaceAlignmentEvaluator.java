package mlllib.evaluator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.util.Pair;
import org.apache.hadoop.hdfs.server.namenode.HostFileManager.EntrySet;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;

import data.model.TVDataSet;
import data.model.TVEvent;
import data.recsys.mapper.MappedIds;
import data.utility.TVDataSetUtilities;
import mllib.recommender.SpaceAlignmentRecommender;
import mllib.recommender.collaborativefiltering.ItemBasedRecommender;
import scala.Tuple2;

public class SpaceAlignmentEvaluator <T extends TVEvent>{
	
	TVDataSet<T> trainingSet;
	TVDataSet<T> testSet;
	SpaceAlignmentRecommender<T> actualRecommender;
	ItemBasedRecommender<T> expectedRecommender;
	
	Map<Integer, Vector> originalsNewItemsIds;
	MappedIds trainingSetMap;
	MappedIds testSetMap;
	
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
		originalsNewItemsIds = weekFourDayOne.mapToPair(tvEvent -> new Tuple2<Integer, Vector>(tvEvent.getProgramID(), tvEvent.getProgramFeatureVector())).reduceByKey((arg1, arg2) -> arg1).collectAsMap();
		initializeMap();
		this.measures = measures;
	}
	
	private void initializeMap(){
		trainingSetMap = null;
		if(trainingSet instanceof MappedIds){
			trainingSetMap = (MappedIds) trainingSet;
		}
		testSetMap = null;
		if(testSetMap instanceof MappedIds){
			testSetMap = (MappedIds) testSet;
		}
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
		int numberOfUsers = trainingSet.getNumberOfUsers();
		for(Entry<Integer,Vector> originalItemIdsContent : originalsNewItemsIds.entrySet()){
			int originalNewItemId = originalItemIdsContent.getKey();
			Vector newItemContent  = originalItemIdsContent.getValue();
			Tuple2<Integer, Integer> mappedItemIds = getMappedItemIds(originalNewItemId);
			for(int i = 0; i < numberOfUsers; i++){
				int originalUserId = trainingSetMap == null ? i : trainingSetMap.getOriginalUserID(i);
				Tuple2<Integer, Integer> mappedUserIds = getMappedUserIds(originalUserId);
				List<Pair<Integer, Double>> actualNeighbours = actualRecommender.predictNewItemNeighborhoodForUser(newItemContent, mappedUserIds._1(), 50);
				List<Pair<Integer, Double>> expectedNeighbours = expectedRecommender.getItemNeighborhoodForUser(mappedUserIds._2(), mappedItemIds._2(), 50);
			}
		}
	}
	
	private void evaluateMeanAveragePrecision(){
		
	}
	
	private void evaluateMeanAverageRecall(){
		
	}
	
	private Tuple2<Integer, Integer> getMappedItemIds(int originalItemId){
		int trainingSetId = trainingSetMap == null ? originalItemId : trainingSetMap.getMappedProgramID(originalItemId);
		int testSetId = testSetMap == null ? originalItemId : testSetMap.getMappedProgramID(originalItemId);
		return new Tuple2<Integer, Integer>(trainingSetId, testSetId);
	}
	
	private Tuple2<Integer, Integer> getMappedUserIds(int originalUserId){
		int trainingSetId = trainingSetMap == null ? originalUserId : trainingSetMap.getMappedUserID(originalUserId);
		int testSetId = testSetMap == null ? originalUserId : testSetMap.getMappedUserID(originalUserId);
		return new Tuple2<Integer, Integer>(trainingSetId, testSetId);
	}
}
