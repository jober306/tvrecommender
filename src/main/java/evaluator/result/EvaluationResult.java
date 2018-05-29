package evaluator.result;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.data.TVProgram;
import model.data.User;
import model.information.Information;

/**
 * Class that encapsulates results of an evaluation for a particular metric.
 * @author Jonathan Bergeron
 *
 */
public class EvaluationResult<U extends User, P extends TVProgram> implements Information, Serializable{
	
	private static final long serialVersionUID = 1L;

	final Map<U, List<P>> userRecommendations;
	final Map<String, MetricResults<U>> metricsResults;
	
	final EvaluationInfo evaluationInfo;
	
	/**
	 * Constructor of the class
	 * @param userId The tested user id
	 * @param score The evaluation score obtained
	 */
	public EvaluationResult(Map<U, List<P>> userRecommendations, List<MetricResults<U>> metricsResults, EvaluationInfo evaluationInfo) {
		this.userRecommendations = userRecommendations;
		this.metricsResults = metricsResults.stream().collect(Collectors.toMap(MetricResults::metricName, java.util.function.Function.identity()));
		this.evaluationInfo = evaluationInfo;
	}
	
	public Map<U, List<P>> userRecommendations(){
		return this.userRecommendations;
	}
	
	public List<P> userRecommendation(U user) {
		return userRecommendations.get(user);
	}
	
	public List<U> topNUsers(String metricName, int n){
		return metricsResults.get(metricName)
				.userScores().entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.limit(n)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}
	
	/**
	 * Method that returns the tested user id.
	 * @return The tested user id.
	 */
	public Map<String, MetricResults<U>> metricsResults() {
		return this.metricsResults;
	}
	
	public MetricResults<U> metricResult(String metricName) {
		return metricsResults.get(metricName);
	}
	
	public EvaluationInfo evaluationInfo(){
		return evaluationInfo;
	}
	
	public String generateFileName(){
		return evaluationInfo.generateFileName();
	}

	@Override
	public String asString() {
		final NumberFormat formatter = new DecimalFormat("#0.000");
		StringBuilder sb = new StringBuilder();
		sb.append(evaluationInfo.asString());
		sb.append("\nEvaluation Results\n");
		for(MetricResults<U> metricResult : metricsResults.values()){
			String metricName = metricResult.metricName();
			double meanScore = metricResult.mean();
			String formattedMeanScore = formatter.format(meanScore);
			sb.append(metricName);
			sb.append(": ");
			sb.append(formattedMeanScore);
			sb.append("\n");
		}
		return sb.toString();
	}
}
