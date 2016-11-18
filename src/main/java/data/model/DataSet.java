package data.model;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import mllib.model.DistributedUserItemMatrix;
import recommender.model.UserItemMatrix;

public interface DataSet {
	
	//------Recommender model convertion method----------
	public UserItemMatrix convertToUserItemMatrix();
	
	//----------ML lib convertion methods----------------
	public JavaRDD<Rating> convertToMLlibRatings();
	public DistributedUserItemMatrix convertToDistUserItemMatrix();
	public IndexedRowMatrix getContentMatrix();
	
	//--------General Utilities methods--------------------
	public int getNumberOfUsers();
	public int getNumberOfItems();
	public List<Integer> getAllUserIds();
	public List<Integer> getAllProgramIds();
	public boolean isEmpty();
}
