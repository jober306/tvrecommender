package evaluator.result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricResults {
	
	final Map<Integer, Double> usersScore;
	
	public MetricResults(Map<Integer, Double> usersScore) {
		this.usersScore = usersScore;
	}
	
	public MetricResults(List<SingleUserResult> results) {
		this.usersScore = results.stream().collect(Collectors.toMap(SingleUserResult::userId, SingleUserResult::score));
	}
	
	public Map<Integer, Double> usersScore(){
		return this.usersScore;
	}
}
