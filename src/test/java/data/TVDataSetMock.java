package data;

import java.util.List;

import model.DistributedUserItemMatrix;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.recommendation.Rating;

import data.feature.FeatureExtractor;

public class TVDataSetMock extends TVDataSet<TVEventMock> {

	private static final long serialVersionUID = 1L;

	public boolean mockInitialized;

	public TVDataSetMock(JavaRDD<TVEventMock> eventsData, JavaSparkContext sc) {
		super(eventsData, sc);
	}

	@Override
	protected void initialize() {
		mockInitialized = true;
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
	public IndexedRowMatrix getContentMatrix(
			FeatureExtractor<?, TVEventMock> extractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(TVEventMock event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNumberOfUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfItems() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> getProgramIndexesSeenByUser(int userIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JavaRDD<TVEventMock>[] splitTVEventsRandomly(double[] ratios) {
		// TODO Auto-generated method stub
		return null;
	}

}
