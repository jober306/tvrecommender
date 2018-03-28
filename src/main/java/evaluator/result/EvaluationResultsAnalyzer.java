package evaluator.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import evaluator.metric.EvaluationMetric;
import scala.Tuple2;

public class EvaluationResultsAnalyzer {
	
	static final Tuple2<Integer, Double> DEFAULT_GEOMETRIC_MEAN_RESULT = new Tuple2<Integer, Double>(1, 0.0d);
	
	final EvaluationResults evaluationResults;
	
	public EvaluationResultsAnalyzer(EvaluationResults evaluationResults) {
		this.evaluationResults = evaluationResults;
	}
	
	public EvaluationResultsAnalyzer(EvaluationMetric<?> metric, MetricResults metricResults) {
		Map<EvaluationMetric<?>, MetricResults> metricResultsMap = new HashMap<EvaluationMetric<?>, MetricResults>();
		metricResultsMap.put(metric, metricResults);
		this.evaluationResults = new EvaluationResults(metricResultsMap);
	}
	
	public Map<EvaluationMetric<?>, Double> means() {
		return evaluationResults.usersScorePerMetric().entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> mean(entry.getValue())));
	}
	
	public static double mean(MetricResults result) {
		return result.usersScore().entrySet().stream()
				.mapToDouble(Entry::getValue)
				.average()
				.orElse(0.0d);
	}
	
	public Map<EvaluationMetric<?>, Double> geometricMeans() {
		return evaluationResults.usersScorePerMetric().entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> geometricMean(entry.getValue())));
	}
	
	public static double geometricMean(MetricResults result) {
		Tuple2<Integer, Double> countProduct = result.usersScore().entrySet().stream()
				.map(Entry::getValue)
				.map(score -> new Tuple2<Integer, Double>(1, score))
				.reduce((countScore1, countScore2) -> new Tuple2<Integer, Double>(countScore1._1() + countScore2._1(), countScore1._2() * countScore2._2()))
				.orElse(DEFAULT_GEOMETRIC_MEAN_RESULT);
		return Math.pow(countProduct._2(), 1.0d / countProduct._1());
	}
}
