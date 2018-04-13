package evaluator.result;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import evaluator.information.Information;

/**
 * Class that encapsulates results of an evaluation for a particular metric.
 * @author Jonathan Bergeron
 *
 */
public class EvaluationResult implements Information, Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * The tested user id
	 */
	final List<MetricResults> metricsResults;
	final EvaluationInfo evaluationInfo;
	
	/**
	 * Constructor of the class
	 * @param userId The tested user id
	 * @param score The evaluation score obtained
	 */
	public EvaluationResult(List<MetricResults> metricsResults, EvaluationInfo evaluationInfo) {
		this.metricsResults = metricsResults;
		this.evaluationInfo = evaluationInfo;
	}
	
	/**
	 * Method that returns the tested user id.
	 * @return The tested user id.
	 */
	public List<MetricResults> metricsResults() {
		return this.metricsResults;
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
		for(MetricResults metricResult : metricsResults){
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
	
	public static EvaluationResult fromFile(String evaluationResultPath){
		EvaluationResult result = null;
		try {
			FileInputStream fis = new FileInputStream(evaluationResultPath);
			ObjectInputStream oos = new ObjectInputStream(fis);
		    result = (EvaluationResult) oos.readObject();
		    oos.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
