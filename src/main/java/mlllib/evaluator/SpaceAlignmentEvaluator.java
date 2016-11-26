package mlllib.evaluator;

import static data.utility.TVDataSetUtilities.*;
import static list.utility.ListUtilities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;

import data.model.TVDataSet;
import data.model.TVEvent;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.mapper.MappedIds;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import mllib.recommender.SpaceAlignmentRecommender;
import mllib.recommender.collaborativefiltering.ItemBasedRecommender;
import scala.Tuple2;

/**
 * Class that evaluate the space alignment recommender on a given data set.
 * The evaluator uses one week to train and the evaluation is made on the next day.
 * @author Jonathan Bergeron
 *
 * @param <T> A class extending TVEvent on which the data set is built.
 */
public class SpaceAlignmentEvaluator <T extends TVEvent>{
	
	/**
	 * The training set on which the space alignment recommender will be trained.
	 */
	TVDataSet<T> trainingSet;
	
	/**
	 * The test set on which the item based recommender will be trained.
	 */
	TVDataSet<T> testSet;
	
	/**
	 * The space alignment recommender trained on training set.
	 */
	SpaceAlignmentRecommender<T> actualRecommender;
	
	/**
	 * The item based (collaborative filtering) recommender builded on test set.
	 */
	ItemBasedRecommender<T> expectedRecommender;
	
	/**
	 * The list of tuple containing the original ids and content vector of new tv programs on the test day.
	 */
	List<Tuple2<Integer, Vector>> originalsNewItemsIds;
	
	/**
	 * Attributes indicating if whether or not the ids have been mapped by the recommenders.
	 */
	boolean trainingSetIdsMapped;
	boolean testSetIdsMapped;
	
	/**
	 * The map of user/program/event ids of the recommenders. If no map was used the map is set to null.
	 */
	MappedIds trainingSetMap;
	MappedIds testSetMap;
	
	/**
	 * The array of measures to evaluate.
	 */
	EvaluationMeasure[] measures;
	
	/**
	 * The map containing results for each evaluation measure in measures.
	 */
	Map<EvaluationMeasure, Double> evaluationResults;
	
	/**
	 * The week that will be used to train the space alignment recommender, the test day will be the next day after that week.
	 */
	int week;
	
	/**
	 * The rank constraint needed by the space alignment recommender.
	 */
	int r;
	
	/**
	 * Constructor of the SpaceAlignmentEvaluator.
	 * @param tvDataSet A data set of tv events with more than just a week.
	 * @param measures The array of evaluation measures that will be calculated.
	 * @param week The week on which the training will be made.
	 * @param r The rank constraint needed by the space alignment recommender.
	 */
	public SpaceAlignmentEvaluator(TVDataSet<T> tvDataSet, EvaluationMeasure[] measures, int week, int r){
		buildTrainingAndTestSet(tvDataSet);
		buildRecommenders();
		initializeMap();
		this.measures = measures;
		this.evaluationResults = new HashMap<EvaluationMeasure, Double>();
		this.week = week;
		this.r = r;
	}
	
	/**
	 * Method that evaluates all the evaluation measures given in measures. The results are stored in the evaluationResults map.
	 */
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
	
	/**
	 * Method that returns the results of the different given measures. 
	 * Make sure to call evaluate before calling this method otherwise it will be empty.
	 * @return A map containing the results for each given evaluation measure.
	 */
	public Map<EvaluationMeasure, Double> getResults(){
		return evaluationResults;
	}
	
	private void buildTrainingAndTestSet(TVDataSet<T> tvDataSet){
		JavaSparkContext sc = tvDataSet.getJavaSparkContext();
		JavaRDD<T> fullDataSet = filterByMinTimeView(tvDataSet.getEventsData(), 7);
		JavaRDD<T> week3 = filterByIntervalOfWeek(fullDataSet, week, week);
		JavaRDD<T> weekFourDayOne = filterByIntervalOfDay(filterByIntervalOfWeek(fullDataSet, week+1, week+1),1,1);
		trainingSet = tvDataSet.buildDataSetFromRawData(week3, sc);
		testSet = tvDataSet.buildDataSetFromRawData(week3.union(weekFourDayOne), sc);
		originalsNewItemsIds = weekFourDayOne.mapToPair(tvEvent -> new Tuple2<Integer, Vector>(tvEvent.getProgramID(), tvEvent.getProgramFeatureVector())).reduceByKey((arg1, arg2) -> arg1).collect();
	}
	
	private void initializeMap(){
		trainingSetIdsMapped = trainingSet instanceof MappedIds;
		testSetIdsMapped = testSet instanceof MappedIds;
		trainingSetMap = trainingSetIdsMapped ? (MappedIds) trainingSet : null;
		testSetMap = testSetIdsMapped ? (MappedIds) testSet : null;
	}
	
	private void buildRecommenders(){
		actualRecommender = new SpaceAlignmentRecommender<T>(trainingSet, r);
		expectedRecommender = new ItemBasedRecommender<T>(testSet);
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
		SpaceAlignmentEvaluator<RecsysTVEvent> evaluator = new SpaceAlignmentEvaluator<RecsysTVEvent>(dataSet, measures,3,4);
		evaluator.evaluate();
		System.out.println("RESULT: " + evaluator.getResults().get(EvaluationMeasure.NEIGHBOURHOOD_COVERAGE));
	}
}
