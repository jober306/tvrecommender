package data;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.data.feature.FeatureExtractor;
import model.matrix.DistributedUserTVProgramMatrix;
import model.matrix.LocalUserTVProgramMatrix;

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
	public DistributedUserTVProgramMatrix<User, TVProgram> convertToDistUserItemMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalUserTVProgramMatrix<User, TVProgram> convertToLocalUserItemMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexedRowMatrix getContentMatrix(FeatureExtractor<? super TVProgram, ? super TVEvent<User, TVProgram>> extractor) {
		// TODO Auto-generated method stub
		return null;
	}

}
