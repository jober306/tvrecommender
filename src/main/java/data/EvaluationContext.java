package data;

import static util.TVDataSetUtilities.createSubDataSet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.Tuple4;

/**
 * Class that represents a context in which a recommender lives.
 * The context is used to evaluate the recommender.
 * @author Jonathan Bergeron
 *
 */
public class EvaluationContext<T extends TVProgram, U extends TVEvent> extends Context<T, U>{
	
	final LocalDateTime testStartTime;
	final LocalDateTime testEndTime;
	final TVDataSet<T, U> testSet;
	final List<T> testPrograms;
	
	final Map<Integer, List<Integer>> groundTruth;
	
	public EvaluationContext(EPG<T> epg, TVDataSet<T, U> events,
			LocalDateTime testStartTime, LocalDateTime testEndTime){
		super(epg, events);
		this.testStartTime = testStartTime;
		this.testEndTime = testEndTime;
		this.testPrograms = createTestPrograms(testStartTime, testEndTime);
		this.testSet = createSubDataSet(events, testStartTime, testEndTime);
		this.groundTruth = createGroundTruth();
	}
	
	public EvaluationContext(EPG<T> epg, TVDataSet<T, U> events, 
			LocalDateTime trainingStartTime, LocalDateTime trainingEndTime, 
			LocalDateTime testStartTime, LocalDateTime testEndTime){
		super(epg, events, trainingStartTime, trainingEndTime);
		this.testStartTime = testStartTime;
		this.testEndTime = testEndTime;
		this.testPrograms = createTestPrograms(testStartTime, testEndTime);
		this.testSet = createSubDataSet(events, testStartTime, testEndTime);
		this.groundTruth = createGroundTruth();
	}
	
	public EvaluationContext(EPG<T> epg, TVDataSet<T, U> events, 
			Tuple4<LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime> trainingTestingTimes){
		this(epg, events, trainingTestingTimes._1(), trainingTestingTimes._2(), trainingTestingTimes._3(), trainingTestingTimes._4());
	}
	
	public LocalDateTime testStartTime(){
		return this.testStartTime;
	}
	
	public LocalDateTime testEndTime(){
		return this.testEndTime;
	}
	
	public TVDataSet<T, U> getTestSet(){
		return this.testSet;
	}
	
	public List<T> getTestPrograms(){
		return this.testPrograms;
	}
	
	public Map<Integer, List<Integer>> getGroundTruth(){
		return this.groundTruth;
	}
	
	private Map<Integer, List<Integer>> createGroundTruth(){
		Map<Integer, List<Integer>> groundTruth = initializeGroundTruth();
		testSet.getEventsData().collect().stream().forEach(event -> addEvent(groundTruth, event));
		return groundTruth;
	}
	
	private Map<Integer, List<Integer>> initializeGroundTruth(){
		Map<Integer, List<Integer>> groundTruth = new HashMap<Integer, List<Integer>>();
		for(int userId : testSet.getAllUserIds()){
			groundTruth.put(userId, new ArrayList<Integer>());
		}
		return groundTruth;
	}
	
	private void addEvent(Map<Integer, List<Integer>> groundTruth, TVEvent event){
		groundTruth.get(event.getUserID()).add(event.getProgramId());
	}
	
	private List<T> createTestPrograms(LocalDateTime testStartTime, LocalDateTime testEndTime){
		return getEPG().getListProgramsBetweenTimes(testStartTime, testEndTime);
	}
	
	@Override
	public void close() {
		super.close();
		trainingSet.close();
	}
}
