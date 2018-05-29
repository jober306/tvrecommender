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
 * @param <U> The type of user.
 *
 * @param <P> The type of tv program.
 * 
 * @param <E> The type of tv event.
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
	 * Constructor of a tv recommender evaluator. The generation of evaluation information is set to false.
	 * @param recommender The recommender to be evaluated.
	 * @param metrics The measures with respect to the recommender will be tested.
	 */
	public TVRecommenderEvaluator(TVRecommender<U, P, E> recommender, Set<EvaluationMetric<U, P>> metrics) {
		this.metrics = metrics;
		this.recommender = recommender;
	}
	
	public Set<EvaluationResult<U, P>> evaluateWithIncreasingTrainingSize(EPG<P> epg, TVDataSet<U, P, E> events, LocalDateTime startTime, LocalDateTime endTime){
		DateTimeRange trainingStartTimes = new DateTimeRange(startTime, startTime, Duration.ZERO);
		DateTimeRange trainingEndTimes = new DateTimeRange(startTime.plusDays(1), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(startTime.plusDays(1), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(startTime.plusDays(2), endTime.minusDays(1), Duration.ofDays(1));
		return evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
	}
	
	public Set<EvaluationResult<U, P>> evaluateMovingTimeWindow(EPG<P> epg, TVDataSet<U, P, E> events, LocalDateTime startTime, Period window, LocalDateTime endTime){
		DateTimeRange trainingStartTimes = new DateTimeRange(startTime, endTime.minus(window).minusDays(1), Duration.ofDays(1));
		DateTimeRange trainingEndTimes = new DateTimeRange(startTime.plus(window), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingStartTimes = new DateTimeRange(startTime.plus(window), endTime.minusDays(1), Duration.ofDays(1));
		DateTimeRange testingEndTimes = new DateTimeRange(startTime.plus(window).plusDays(1), endTime, Duration.ofDays(1));
		return evaluateTimeSeries(epg, events, trainingStartTimes, trainingEndTimes, testingStartTimes, testingEndTimes);
	}
	
	public Set<EvaluationResult<U, P>> evaluateTimeSeries(EPG<P> epg, TVDataSet<U, P, E> events, DateTimeRange trainingStartTimes, DateTimeRange trainingEndTimes, DateTimeRange testingStartTimes, DateTimeRange testingEndTimes){
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
	
	public EvaluationResult<U, P> evaluate(EvaluationContext<U, P, E> context){
		recommender.setContext(context);
		System.out.print("Training recommender...");
		Instant trainingStartTime = Instant.now();
		recommender.train();
		Instant trainingEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(trainingStartTime, trainingEndTime) + " seconds!");
		EvaluationResult<U, P> result = evaluate();
		return result;
	}
	
	public EvaluationResult<U, P> evaluate() {
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
		List<MetricResults<U>> results = metrics.stream().map(metric -> {
				Map<U, Double> userScores = StreamUtilities.toMapAverage(testedUserRecommendations.stream(), Recommendations::user, recommendation -> metric.evaluate(recommendation, context));
				return new MetricResults<>(metric.name(), userScores);
			}
			).collect(Collectors.toList());
		Instant metricEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(metricStartTime, metricEndTime) + " seconds!");
		Instant infoStartTime = Instant.now();
		System.out.print("Creating evaluation info...");
		Instant infoEndTime = Instant.now();
		System.out.println("Done in " + SECONDS.between(infoStartTime, infoEndTime) + " seconds!");
		Map<U, List<P>> userRecommendationsAsMap = testedUserRecommendations.stream()
				.collect(Collectors.toMap(Recommendations::user, Recommendations::get));
		return new EvaluationResult<>(userRecommendationsAsMap, results, new EvaluationInfo(recommender, context));
	}
}
