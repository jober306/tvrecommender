package evaluator.result;

import java.io.Serializable;
import java.util.Map;
import model.data.User;

import java.util.Map.Entry;

import scala.Tuple2;

public class MetricResults<U extends User> implements Serializable{

	private static final long serialVersionUID = 1L;

	static final Tuple2<Integer, Double> DEFAULT_GEOMETRIC_MEAN_RESULT = new Tuple2<Integer, Double>(1, 0.0d);
	
	final String metricName;
	final Map<U, Double> userScores;
	
	public MetricResults(String metricName, Map<U, Double> userScores) {
		this.metricName = metricName;
		this.userScores = userScores;
	}
	
	public String metricName(){
		return this.metricName;
	}
	
	public Map<U, Double> userScores(){
		return this.userScores;
	}
	
	public Double userScore(U user){
		return userScores.get(user);
	}
	
	public double mean() {
		return userScores.entrySet().stream()
				.mapToDouble(Entry::getValue)
				.filter(score -> !Double.isNaN(score))
				.average()
				.orElse(0.0d);
	}
	
	public double variance() {
		double expectedSqaredScore = userScores.entrySet().stream()
				.mapToDouble(Entry::getValue)
				.filter(score -> !Double.isNaN(score))
				.map(score -> score * score)
				.average()
				.orElse(0.0d);
		double expectedScore = mean();
		return expectedSqaredScore - (expectedScore * expectedScore);
	}
	
	public double std() {
		return Math.sqrt(variance());
	}
	
	public double geometricMean() {
		Tuple2<Integer, Double> countProduct = userScores.entrySet().stream()
				.map(Entry::getValue)
				.filter(score -> !Double.isNaN(score))
				.map(score -> new Tuple2<Integer, Double>(1, score))
				.reduce((countScore1, countScore2) -> new Tuple2<Integer, Double>(countScore1._1() + countScore2._1(), countScore1._2() * countScore2._2()))
				.orElse(DEFAULT_GEOMETRIC_MEAN_RESULT);
		return Math.pow(countProduct._2(), 1.0d / countProduct._1());
	}
}
