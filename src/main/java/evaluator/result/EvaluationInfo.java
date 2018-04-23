package evaluator.result;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import data.EvaluationContext;
import data.TVDataSetInfo;
import evaluator.information.AbstractInformation;
import recommender.TVRecommender;
import recommender.RecommenderInfo;

public class EvaluationInfo extends AbstractInformation implements Serializable{
	 
	private static final long serialVersionUID = 1L;

	RecommenderInfo recommenderInfo;
	
	TVDataSetInfo trainingTvDataSetInfo;
	TVDataSetInfo testTvDataSetInfo;
	
	LocalDateTime trainingStartTime;
	LocalDateTime trainingEndTime;
	
	LocalDateTime testStartTime;
	LocalDateTime testEndTime;
	
	public EvaluationInfo(RecommenderInfo recommenderInfo, TVDataSetInfo trainingTvDataSetInfo, TVDataSetInfo testTvDataSetInfo, LocalDateTime trainingStartTime, LocalDateTime trainingEndTime, LocalDateTime testStartTime, LocalDateTime testEndTime){
		this.recommenderInfo = recommenderInfo;
		this.trainingTvDataSetInfo = trainingTvDataSetInfo;
		this.testTvDataSetInfo = testTvDataSetInfo;
		this.trainingStartTime = trainingStartTime;
		this.trainingEndTime = trainingEndTime;
		this.testStartTime = testStartTime;
		this.testEndTime = testEndTime;
	}
	
	public EvaluationInfo(TVRecommender<?,?,?> recommender, EvaluationContext<?, ?> context){
		this(recommender.info(), context.getTrainingSet().info(), context.getTestSet().info(), context.getTrainingStartTime(), context.getTrainingEndTime(), context.testStartTime(), context.testEndTime());
	}

	public LocalDateTime getTrainingStartTime() {
		return trainingStartTime;
	}

	public LocalDateTime getTrainingEndTime() {
		return trainingEndTime;
	}

	public LocalDateTime getTestStartTime() {
		return testStartTime;
	}

	public LocalDateTime getTestEndTime() {
		return testEndTime;
	}
	
	public String generateFileName(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
		return trainingStartTime.format(formatter) + "-" + trainingEndTime.format(formatter) + "--" + testStartTime.format(formatter) + "-" + testEndTime.format(formatter);
	}

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Recommender Information\n");
		sb.append(recommenderInfo.asString() + "\n");
		sb.append("\nTraining Set Information\n");
		sb.append("Training time: " + trainingStartTime.toString() + " to " + trainingEndTime.toString() + "\n");
		sb.append(trainingTvDataSetInfo.asString() + "\n");
		sb.append("\nTest Set Information\n");
		sb.append("Testing time: " + testStartTime.toString() + " to " + testEndTime.toString() + "\n");
		sb.append(testTvDataSetInfo.asString() + "\n");
		return sb.toString();
	}
}
