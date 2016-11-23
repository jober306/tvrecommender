package data.model;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import mllib.model.DistributedUserItemMatrix;
import recommender.model.UserItemMatrix;

public abstract class TVDataSet<T extends TVEvent>{
	
	protected JavaRDD<T> eventsData;
	protected JavaSparkContext sc;
	
	//------Data set constructor------------------------
	public TVDataSet(JavaRDD<T> eventsData, JavaSparkContext sc){
		this.eventsData = eventsData;
		this.sc = sc;
	}
	
	abstract public  TVDataSet<T> buildDataSetFromRawData(JavaRDD<T> eventsData, JavaSparkContext sc);
	
	//------Recommender model convertion method----------
	abstract public UserItemMatrix convertToUserItemMatrix();
	
	//----------ML lib convertion methods----------------
	abstract public JavaRDD<Rating> convertToMLlibRatings();
	abstract public DistributedUserItemMatrix convertToDistUserItemMatrix();
	abstract public IndexedRowMatrix getContentMatrix();
	
	//--------General Utilities methods--------------------
	/**
	 * Getter method that return the data attached to this data set.
	 * 
	 * @return The java RDD containing all the recsys tv event.
	 */
	public JavaRDD<T> getEventsData(){
		return eventsData;
	}
	
	/**
	 * Method that returns the java spark context used to load this data set.
	 * @return The java spark context used to load this data set.
	 */
	public JavaSparkContext getJavaSparkContext(){
		return sc;
	}
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
