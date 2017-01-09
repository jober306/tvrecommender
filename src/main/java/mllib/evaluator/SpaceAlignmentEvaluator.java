package mllib.evaluator;

import static data.utility.TVDataSetUtilities.*;
import static list.utility.ListUtilities.*;
import static data.recsys.model.RecsysTVDataSet.START_TIME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;

import data.feature.FeatureExtractor;
import data.model.EPG;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.mapper.MappedIds;
import data.recsys.model.RecsysTVDataSet;
import data.recsys.model.RecsysTVEvent;
import data.recsys.model.RecsysTVProgram;
import mllib.recommender.SpaceAlignmentRecommender;
import mllib.recommender.collaborativefiltering.ItemBasedRecommender;
import scala.Tuple2;
import spark.utilities.SparkUtilities;

/**
 * Class that evaluate the space alignment recommender on a given data set.
 * The evaluator uses one week to train and the evaluation is made on the next day.
 * @author Jonathan Bergeron
 *
 * @param <T> A class extending TVEvent on which the data set is built.
 */
public class SpaceAlignmentEvaluator <T extends TVProgram, U extends TVEvent>{
	
	/**
	 * The electronic programming guide.
	 */
	EPG<T> epg;
	
	/**
	 * The training set on which the space alignment recommender will be trained.
	 */
	TVDataSet<U> trainingSet;
	
	/**
	 * The test set on which the item based recommender will be trained.
	 */
	TVDataSet<U> testSet;
	
	/**
	 * The space alignment recommender trained on training set.
	 */
	SpaceAlignmentRecommender<T,U> actualRecommender;
	
	/**
	 * The item based (collaborative filtering) recommender builded on test set.
	 */
	ItemBasedRecommender<T,U> expectedRecommender;
	
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
	 * Feature extractor used for this date set and epg programs.
	 */
	FeatureExtractor<T, U> extractor;
	
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
	 * The neighbourhood size used when calculating similarity between items.
	 */
	int neighbourhoodSize;
	
	/**
	 * The number of results returned when recommending.
	 */
	int numberOfResults;
	
	/**
	 * Constructor of the SpaceAlignmentEvaluator.
	 * @param tvDataSet A data set of tv events with more than just a week.
	 * @param measures The array of evaluation measures that will be calculated.
	 * @param week The week on which the training will be made.
	 * @param r The rank constraint needed by the space alignment recommender.
	 */
	public SpaceAlignmentEvaluator(EPG<T> epg, TVDataSet<U> tvDataSet, FeatureExtractor<T,U> extractor, EvaluationMeasure[] measures, int week, int r, int neighbourhoodSize, int numberOfResults){
		this.epg = epg;
		this.week = week;
		this.r = r;
		this.neighbourhoodSize = neighbourhoodSize;
		this.numberOfResults = numberOfResults;
		this.extractor = extractor;
		buildTVDataSets(tvDataSet);
		buildRecommenders();
		initializeMap();
		this.measures = measures;
		this.evaluationResults = new HashMap<EvaluationMeasure, Double>();
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
	
	private void buildTVDataSets(TVDataSet<U> tvDataSet){
		JavaSparkContext sc = tvDataSet.getJavaSparkContext();
		JavaRDD<U> fullDataSet = filterByMinTimeView(tvDataSet.getEventsData(), 7);
		JavaRDD<U> week3 = filterByDateTime(fullDataSet, START_TIME.plusWeeks(week), START_TIME.plusWeeks(week+1));
		JavaRDD<U> weekFourDayOne = filterByDateTime(fullDataSet, START_TIME.plusWeeks(week+1), START_TIME.plusWeeks(week+1).plusDays(1));
		trainingSet = tvDataSet.buildDataSetFromRawData(week3, sc);
		testSet = tvDataSet.buildDataSetFromRawData(week3.union(weekFourDayOne), sc);
	}
	
	private void initializeMap(){
		trainingSetIdsMapped = trainingSet instanceof MappedIds;
		testSetIdsMapped = testSet instanceof MappedIds;
		trainingSetMap = trainingSetIdsMapped ? (MappedIds) trainingSet : null;
		testSetMap = testSetIdsMapped ? (MappedIds) testSet : null;
	}
	
	private void buildRecommenders(){
		actualRecommender = new SpaceAlignmentRecommender<T,U>(epg, trainingSet, extractor, r, neighbourhoodSize);
		expectedRecommender = new ItemBasedRecommender<T,U>(epg, testSet);
	}
	
	private void evaluateNeighbourhoodCoverage(){
		int n = 50;
		double totalCoverage = 0.0d;
		int totalNewItemNumbers = originalsNewItemsIds.size();
		for(Tuple2<Integer,Vector> originalItemIdsContent : originalsNewItemsIds){
			int originalNewItemId = originalItemIdsContent._1();
			Vector newItemContent  = originalItemIdsContent._2();
			int mappedExpectedItemId = testSetMap == null ? originalNewItemId : testSetMap.getMappedProgramID(originalNewItemId);
			System.out.println("CALCULATING NEIGHBOUR FOR SPACE ALIGNMENT RECOMMENDER...");
			List<Integer> actualNeighboursMappedID = getFirstArgument(actualRecommender.predictNewItemNeighbourhood(newItemContent, n));
			System.out.println("CALCULATING NEIGHBOUR FOR ITEM BASED RECOMMENDER...");
			List<Integer> expectedNeighboursMappedID = getFirstArgument(expectedRecommender.predictItemNeighbourhood(mappedExpectedItemId, n));
			expectedNeighboursMappedID = substract(expectedNeighboursMappedID, getFirstArgument(originalsNewItemsIds));
			List<Integer> actualNeighboursOriginalID = trainingSetIdsMapped ? getOriginalItemIds(trainingSetMap, actualNeighboursMappedID) : actualNeighboursMappedID;
			List<Integer> expectedNeighboursOriginalID = testSetIdsMapped ? getOriginalItemIds(testSetMap, expectedNeighboursMappedID) : expectedNeighboursMappedID;
			double neighbourhoodCoverage = (double)intersection(actualNeighboursOriginalID, expectedNeighboursOriginalID).size() / (double)expectedNeighboursOriginalID.size();
			System.out.println("Coverage for item: " + originalNewItemId + "/" + totalNewItemNumbers + " = " + neighbourhoodCoverage);
			totalCoverage += neighbourhoodCoverage;
		}
		evaluationResults.put(EvaluationMeasure.NEIGHBOURHOOD_COVERAGE, totalCoverage / (double) originalsNewItemsIds.size());
	}
	
	private List<Integer> getOriginalItemIds(MappedIds map, List<Integer> l){
		return l.stream().map(id -> map.getOriginalProgramID(id)).collect(Collectors.toList());
	}
	
	private void evaluateMeanAveragePrecision(){
		int numberOfUsers = trainingSet.getNumberOfUsers();
		double meanAveragePrecision = 0.0d;
		for(int userIndex = 0; userIndex < numberOfUsers; userIndex++){
			int originalUserIndex = trainingSetIdsMapped ? trainingSetMap.getOriginalUserID(userIndex) : userIndex;
			System.out.println("Recommending for user " + originalUserIndex);
			List<Integer> recommendedItemIndexes = actualRecommender.recommend(userIndex, START_TIME.plusWeeks(week+1).plusDays(1),numberOfResults);
			System.out.println("Done");
			List<Integer> originalIdsOfItemsSeenByUser = getProgramIndexesSeenByUser(originalUserIndex);
			double averagePrecision = 0.0d;
			double recommendedItemSize = (double) originalIdsOfRecommendedItemIndexes.size();
			for(int n = 1; n < 10; n++){
				if(originalIdsOfItemsSeenByUser.contains(recommendedItemIndexes.get(n))){
					double intersectionSize = (double)intersection(originalIdsOfRecommendedItemIndexes, originalIdsOfItemsSeenByUser).size();
					averagePrecision += intersectionSize / recommendedItemSize;
				}
			}
			averagePrecision /= recommendedItemSize;
			System.out.println("Mean Average for user: " + userIndex + "/" + numberOfUsers + " is " + averagePrecision);
			meanAveragePrecision += averagePrecision;
		}
		meanAveragePrecision /= (double) numberOfUsers;
		evaluationResults.put(EvaluationMeasure.MEAN_AVERAGE_PRECISION, meanAveragePrecision);
	}
	
	private void evaluateMeanAverageRecall(){
		
	}
	
	public static void main(String[] args){
		EvaluationMeasure[] measures = new EvaluationMeasure[]{EvaluationMeasure.MEAN_AVERAGE_PRECISION};
		JavaSparkContext sc = SparkUtilities.getADefaultSparkContext();
		sc.setLogLevel("ERROR");
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(sc);
		RecsysTVDataSet dataSet = loader.loadDataSet();
		SpaceAlignmentEvaluator<RecsysTVProgram, RecsysTVEvent> evaluator = new SpaceAlignmentEvaluator<RecsysTVProgram, RecsysTVEvent>(dataSet, measures,4,4, 20, 50);
		evaluator.evaluate();
		System.out.println("RESULT: " + evaluator.getResults().get(EvaluationMeasure.MEAN_AVERAGE_PRECISION));
	}
}
