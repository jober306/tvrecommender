package mllib.recommender;

import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;

import scala.Tuple2;
import data.recsys.loader.RecsysTVDataSetLoader;
import data.recsys.model.RecsysTVDataSet;

public class MllibWrapper {

	public static MatrixFactorizationModel train(RecsysTVDataSet dataSet,
			int matrixRank, int numIter) {
		JavaRDD<Rating> ratings = dataSet.convertToMLlibRatings();
		return ALS.train(JavaRDD.toRDD(ratings), matrixRank, numIter);
	}

	public static void evaluateModel(MatrixFactorizationModel model,
			RecsysTVDataSet dataSet) {
		JavaRDD<Rating> ratings = dataSet.convertToMLlibRatings();
		JavaRDD<Tuple2<Object, Object>> userProducts = ratings
				.map(rating -> new Tuple2<Object, Object>(rating.user(), rating
						.product()));
		JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD
				.fromJavaRDD(model
						.predict(JavaRDD.toRDD(userProducts))
						.toJavaRDD()
						.map(rating -> new Tuple2<>(new Tuple2<>(rating.user(),
								rating.product()), rating.rating())));

		JavaRDD<Tuple2<Double, Double>> ratesAndPreds = JavaPairRDD
				.fromJavaRDD(
						ratings.map(rating -> new Tuple2<>(new Tuple2<>(rating
								.user(), rating.product()), rating.rating())))
				.join(predictions).values();
		double MSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(rateAndPred -> {
			Double err = rateAndPred._1() - rateAndPred._2();
			return Object.class.cast(err * err);
		}).rdd()).mean();
		System.out.println("Mean Squared Error = " + MSE);
	}

	public static void main(String[] args) {
		RecsysTVDataSetLoader dataLoader = new RecsysTVDataSetLoader();
		RecsysTVDataSet dataSet = dataLoader.loadDataSet();
		MatrixFactorizationModel model = MllibWrapper.train(dataSet, 10, 20);
		MllibWrapper.evaluateModel(model, dataSet);
	}
}
