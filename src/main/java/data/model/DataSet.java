package data.model;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.Rating;

import recommender.model.UserItemMatrix;

public interface DataSet {
	
	//------Recommender model convertion method----------
	public UserItemMatrix convertToUserItemMatrix();
	
	//----------ML lib convertion methods----------------
	public JavaRDD<Rating> convertToMLlibRatings();
	
	//--------General Utilities methods--------------------
	public int getNumberOfUsers();
	public int getNumberOfItems();
	public List<Integer> getAllUserIds();
	public List<Integer> getAllProgramIds();
	public boolean isEmpty();
}
