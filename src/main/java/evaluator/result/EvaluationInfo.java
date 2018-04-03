package evaluator.result;

import java.time.LocalDateTime;

import data.EvaluationContext;
import data.TVDataSetInfo;
import evaluator.information.Information;
import recommender.AbstractTVRecommender;
import recommender.RecommenderInfo;

public class EvaluationInfo implements Information{
	 
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
	
	public EvaluationInfo(AbstractTVRecommender<?,?,?> recommender, EvaluationContext<?, ?> context){
		this(recommender.info(), context.getTrainingSet().info(), context.getTestSet().info(), context.getTrainingSet().startTime(), context.getTrainingSet().endTime(), context.getTestSet().startTime(), context.getTestSet().endTime());
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
