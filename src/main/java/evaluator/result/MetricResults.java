package evaluator.result;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import util.StreamUtilities;

public class MetricResults {
	
	final Map<Integer, Double> usersScore;
	
	public MetricResults(Map<Integer, Double> usersScore) {
		this.usersScore = usersScore;
	}
	
	public MetricResults(List<SingleUserResult> results) {
		this.usersScore = StreamUtilities.toMapAverage(results.stream(), SingleUserResult::userId, SingleUserResult::score);
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
}
