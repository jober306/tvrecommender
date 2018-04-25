package data;

import static util.TVDataSetUtilities.createSubDataSet;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import scala.Tuple4;

/**
 * Class that represents a context in which a recommender lives.
 * The context is used to evaluate the recommender.
 * @author Jonathan Bergeron
 *
 */
public class EvaluationContext<U extends User, P extends TVProgram, E extends TVEvent<U, P>> extends Context<U, P, E>{
	
	final LocalDateTime testStartTime;
	final LocalDateTime testEndTime;
	
	/**
	 * The testing subset on which the recommender will be evaluated.
	 */
	final TVDataSet<U, P, E> testSet;
	
	/**
	 * All the tv programs occurring during the testing time. 
	 */
	final List<P> testPrograms;
	
	/**
	 * The map containing the set of programs that each user watched.
	 */
	final Map<Integer, Set<P>> groundTruth;
	
	/**
	 * Constructor of this class
	 * @param epg The electronic programming guide.
	 * @param events The events that occured in the epg.
	 * @param trainingStartTime The training start time.
	 * @param trainingEndTime The training end time.
	 * @param testStartTime The testing start time.
	 * @param testEndTime The testing end time.
	 */
	public EvaluationContext(EPG<P> epg, TVDataSet<U, P, E> events, 
			LocalDateTime trainingStartTime, LocalDateTime trainingEndTime, 
			LocalDateTime testStartTime, LocalDateTime testEndTime){
		super(epg, events, trainingStartTime, trainingEndTime);
		this.testStartTime = testStartTime;
		this.testEndTime = testEndTime;
		this.testPrograms = createTestPrograms(testStartTime, testEndTime);
		this.testSet = createSubDataSet(events, testStartTime, testEndTime);
		this.groundTruth = createGroundTruth();
	}
	
	/**
	 * Constructor of this class
	 * @param events The events that occured in the epg.
	 * @param trainingStartTime The training start time.
	 * @param trainingTestingTimes Tuple4 containing respectively: training start time, training end time, testing start time, testing end time.
	 */
	public EvaluationContext(EPG<P> epg, TVDataSet<U, P, E> events, 
			Tuple4<LocalDateTime, LocalDateTime, LocalDateTime, LocalDateTime> trainingTestingTimes){
		this(epg, events, trainingTestingTimes._1(), trainingTestingTimes._2(), trainingTestingTimes._3(), trainingTestingTimes._4());
	}
	
	/**
	 * Method that return the testing start time.
	 * @return The testing start time.
	 */
	public LocalDateTime testStartTime(){
		return this.testStartTime;
	}
	
	/**
	 * Method that return the testing end time.
	 * @return The testing end time.
	 */
	public LocalDateTime testEndTime(){
		return this.testEndTime;
	}
	
	/**
	 * Method that returns the subset used to to test.
	 * @return The tv data set used to test.
	 */
	public TVDataSet<U, P, E> getTestSet(){
		return this.testSet;
	}
	
	/**
	 * Method that returns the list of tv programs occurring during test time.
	 * @return The list of tv programs occurring during test time.
	 */
	public List<P> getTestPrograms(){
		return this.testPrograms;
	}
	
	/**
	 * Method that return the ground truth map.
	 * @return A map containing, for each user, the set of tv program they watched.
	 */
	public Map<Integer, Set<P>> getGroundTruth(){
		return this.groundTruth;
	}

	private Map<Integer, Set<P>> createGroundTruth(){
		Map<Integer, Set<P>> groundTruth = initializeGroundTruth();
		testSet.getEventsData().collect().stream().forEach(event -> addEvent(groundTruth, event));
		return groundTruth;
	}
	
	private Map<Integer, Set<P>> initializeGroundTruth(){
		Map<Integer, Set<P>> groundTruth = new HashMap<Integer, Set<P>>();
		for(int userId : testSet.getAllUserIds()){
			groundTruth.put(userId, new HashSet<P>());
		}
		return groundTruth;
	}
	
	private void addEvent(Map<Integer, Set<P>> groundTruth, E event){
		groundTruth.get(event.getUserID()).add(event.getProgram());
	}
	
	private List<P> createTestPrograms(LocalDateTime testStartTime, LocalDateTime testEndTime){
		return getEPG().getListProgramsBetweenTimes(testStartTime, testEndTime).stream().distinct().collect(Collectors.toList());
	}
	
	@Override
	public void close() {
		super.close();
		testSet.close();
	}
}
