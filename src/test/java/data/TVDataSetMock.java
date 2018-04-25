package data;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.feature.FeatureExtractor;
import model.matrix.DistributedUserItemMatrix;
import model.matrix.LocalUserItemMatrix;

public class TVDataSetMock extends TVDataSet<User, TVProgram, TVEvent<User, TVProgram>> {

	private static final long serialVersionUID = 1L;

	public TVDataSetMock(JavaRDD<TVEvent<User, TVProgram>> eventsData, JavaSparkContext sc) {
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
	public IndexedRowMatrix getContentMatrix(FeatureExtractor<? super TVProgram, ? super TVEvent<User, TVProgram>> extractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

}
