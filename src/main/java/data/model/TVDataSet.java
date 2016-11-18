package data.model;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import mllib.model.DistributedUserItemMatrix;
import recommender.model.UserItemMatrix;

public abstract class TVDataSet<T extends TVEvent>{
	
	//------Recommender model convertion method----------
	abstract public UserItemMatrix convertToUserItemMatrix();
	
	//----------ML lib convertion methods----------------
	abstract public JavaRDD<Rating> convertToMLlibRatings();
	abstract public DistributedUserItemMatrix convertToDistUserItemMatrix();
	abstract public IndexedRowMatrix getContentMatrix();
	
	//--------General Utilities methods--------------------
	abstract public JavaRDD<T> getEventsData();
	abstract public JavaSparkContext getJavaSparkContext();
	abstract public boolean isEmpty();
	abstract public boolean contains(T event);
	abstract public List<Integer> getAllUserIds();
	abstract public List<Integer> getAllProgramIds();
	abstract public List<Integer> getAllEventIds();
	abstract public int getNumberOfUsers();
	abstract public int getNumberOfItems();
	abstract public int count();
	abstract public JavaRDD<T>[] splitTVEventsRandomly(double[] ratios);
}
