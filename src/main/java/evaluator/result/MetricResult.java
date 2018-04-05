package evaluator.result;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import scala.Tuple2;

public class MetricResult {
	
	static final Tuple2<Integer, Double> DEFAULT_GEOMETRIC_MEAN_RESULT = new Tuple2<Integer, Double>(1, 0.0d);
	
	final String metricName;
	final Map<Integer, Double> usersScore;
	
	public MetricResult(String metricName, Map<Integer, Double> usersScore) {
		this.metricName = metricName;
		this.usersScore = usersScore;
	}
	
	public Map<Integer, Double> usersScore(){
		return this.usersScore;
	}
	
	public Optional<Double> userScore(int userId){
		if(this.usersScore.containsKey(userId)){
			return Optional.of(this.usersScore.get(userId));
		}else{
			return Optional.empty();
		}
	}
	
	public double mean() {
		return usersScore.entrySet().stream()
				.mapToDouble(Entry::getValue)
				.average()
				.orElse(0.0d);
	}
	
	public double geometricMean() {
		Tuple2<Integer, Double> countProduct = usersScore.entrySet().stream()
				.map(Entry::getValue)
				.map(score -> new Tuple2<Integer, Double>(1, score))
				.reduce((countScore1, countScore2) -> new Tuple2<Integer, Double>(countScore1._1() + countScore2._1(), countScore1._2() * countScore2._2()))
				.orElse(DEFAULT_GEOMETRIC_MEAN_RESULT);
		return Math.pow(countProduct._2(), 1.0d / countProduct._1());
	}
}
