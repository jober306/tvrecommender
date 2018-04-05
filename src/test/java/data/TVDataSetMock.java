package data;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import data.feature.FeatureExtractor;
import model.DistributedUserItemMatrix;
import model.LocalUserItemMatrix;

public class TVDataSetMock extends TVDataSet<TVProgram, TVEvent> {

	private static final long serialVersionUID = 1L;

	public TVDataSetMock(JavaRDD<TVEvent> eventsData, JavaSparkContext sc) {
		super(eventsData, sc);
	}

	@Override
	public JavaRDD<Rating> convertToMLlibRatings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DistributedUserItemMatrix convertToDistUserItemMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalUserItemMatrix convertToLocalUserItemMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexedRowMatrix getContentMatrix(FeatureExtractor<? extends TVProgram, TVEvent> extractor) {
		// TODO Auto-generated method stub
		return null;
	}

}
