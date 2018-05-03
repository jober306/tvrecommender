package evaluator;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import data.EPG;
import data.EvaluationContext;
import data.TVDataSet;
import evaluator.metric.EvaluationMetric;
import evaluator.result.EvaluationInfo;
import evaluator.result.EvaluationResult;
import evaluator.result.MetricResults;
import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;
import recommender.TVRecommender;
import util.collections.StreamUtilities;
import util.time.DateTimeRange;

/**
 * Class that evaluate a tv recommender on a given data set.
 * 
 * @author Jonathan Bergeron
 *
 * @param <P>
 *            A class extending TVEvent on which the data set and the tv
 *            recommender is built.
 * @param <E>
 *            A class extending TVProgram on which the data set and the tv
 *            recommender is built.
 * @param <R>
 * 			  The type of recommendations made by the recommender.
 */
public class TVRecommenderEvaluator<U extends User, P extends TVProgram, E extends TVEvent<U, P>> {

	/**
	 * The tv recommender to evaluate.
	 */
	final TVRecommender<U, P, E> recommender;

	/**
	 * The array of measures on which evaluation will be based.
	 */
	final Set<EvaluationMetric<U, P>> metrics;
	
	/**
	 * A boolean indicating if the evaluation info must be generated. Can take some times.
	 */
	boolean evaluationInfoGenerated;

	public boolean isEvaluationInfoGenerated() {
		return evaluationInfoGenerated;
	}

	public void setEvaluationInfoGenerated(boolean EvaluationInfoGenerated) {
		this.evaluationInfoGenerated = EvaluationInfoGenerated;
	}

	/**
	 * Constructor of a tv recommender evaluator. The generation of evaluation information is set to false.
	 * @param recommender The recommender to be evaluated.
	 * @param metrics The measures with respect to the recommender will be tested.
	 */
	public TVRecommenderEvaluator(TVRecommender<U, P, E> recommender, Set<EvaluationMetric<U, P>> metrics) {
		this.metrics = metrics;
		this.recommender = recommender;
		this.evaluationInfoGenerated = false;
	}
	
	public TVRecommenderEvaluator(TVRecommender<U, P, E> recommender, Set<EvaluationMetric<U, P>> metrics, boolean evaluationInfoGenerated){
		this.metrics = metrics;
		this.recommender = recommender;
		this.evaluationInfoGenerated = evaluationInfoGenerated;
	}
	
	public Set<EvaluationResult> evaluateWithIncreasingTrainingSize(EPG<P> epg, TVDataSet<U, P, E> events, LocalDateTime startTime, LocalDateTime endTime){
		DateTimeRange trainingStartTimes = new DateTimeRange(startTime, startTime, Duration.ZERO);
		DateTimeRange trainingEndTimes = new DateTimeRange(startTime.plusDays(1), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(startTime.plusDays(1), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(startTime.plusDays(2), endTime.minusDays(1), Duration.ofDays(1));
		return evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
	}
	
	public Set<EvaluationResult> evaluateMovingTimeWindow(EPG<P> epg, TVDataSet<U, P, E> events, LocalDateTime startTime, Period window, LocalDateTime endTime){
		DateTimeRange trainingStartTimes = new DateTimeRange(startTime, endTime.minus(window).minusDays(1), Duration.ofDays(1));
		DateTimeRange trainingEndTimes = new DateTimeRange(startTime.plus(window), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(startTime.plus(window), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(startTime.plus(window).plusDays(1), endTime, Duration.ofDays(1));
		return evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
	}
	
	public Set<EvaluationResult> evaluateTimeSeries(EPG<P> epg, TVDataSet<U, P, E> events, DateTimeRange trainingStartTimes, DateTimeRange trainingEndTimes, DateTimeRange testingStartTimes, DateTimeRange testingEndTimes){
		return StreamUtilities.zip(trainingStartTimes.stream(), trainingEndTimes.stream(), testingStartTimes.stream(), testingEndTimes.stream())
			.map(dateTimes -> {
				System.out.print("Creating evaluation context...");
				Instant evalContextStartTime = Instant.now();
				EvaluationContext<U, P, E> evalContext =  new EvaluationContext<>(epg, events, dateTimes);
				Instant evalContextEndTime = Instant.now();
				System.out.println("Done in " + SECONDS.between(evalContextStartTime, evalContextEndTime) + " seconds!");
				return evalContext;
			})
			.map(this::evaluate)
			.collect(Collectors.toSet());
	}
	
	public EvaluationResult evaluate(EvaluationContext<U, P, E> context){
		System.out.print("Setting context...");
		Instant settingStartTime = Instant.now();
		recommender.setContext(context);
		Instant settingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(settingStartTime, settingEndTime) + " seconds!");
		System.out.print("Training recommender...");
		Instant trainingStartTime = Instant.now();
		recommender.train();
		Instant trainingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(trainingStartTime, trainingEndTime) + " seconds!");
		EvaluationResult result = evaluate();
		return result;
	}
	
	/**
	 * Method that evaluates the recommender on all test users.
	 * @param numberOfRecommendations The number of recommendations the recommender need to output.
	 */
	public EvaluationResult evaluate() {
		EvaluationContext<U, P, E> context = (EvaluationContext<U, P, E>) recommender.getContext();
		Instant recommendingStartTime = Instant.now();
		System.out.print("Recommending for tested users...");
		List<Recommendations<U, P>> testedUserRecommendations = context.getTestSet().allUsers().stream()
			.map(testedUser -> recommender.recommend(testedUser, context.getTestPrograms()))
			.collect(Collectors.toList());
		Instant recommendingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(recommendingStartTime, recommendingEndTime) + " seconds!");
		Instant metricStartTime = Instant.now();
		System.out.print("Evaluating metrics...");
		List<MetricResults> results = metrics.stream().map(metric -> {
				Map<Integer, Double> userScores = StreamUtilities.toMapAverage(testedUserRecommendations.stream(), Recommendations::userId, recommendation -> metric.evaluate(recommendation, context));
				return new MetricResults(metric.name(), userScores);
			}
			).collect(Collectors.toList());
		Instant metricEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(metricStartTime, metricEndTime) + " seconds!");
		Instant infoStartTime = Instant.now();
		EvaluationInfo evaluationInfo = null;
		if(isEvaluationInfoGenerated()){
			System.out.print("Creating evaluation info...");
			evaluationInfo = new EvaluationInfo(recommender, context);
			Instant infoEndTime = Instant.now();
			System.out.println("Done in " + SECONDS.between(infoStartTime, infoEndTime) + " seconds!");
		}
		return new EvaluationResult(results, evaluationInfo);
	}
}
