package mllib.recommender;

import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;

import scala.Tuple2;
import data.recsys.model.RecsysTVDataSet;
import recommender.prediction.Recommender;

public class ALSRecommender implements Recommender{

	public static MatrixFactorizationModel trainExplicit(JavaRDD<Rating> ratings,
			int matrixRank, int numIter) {
		return ALS.train(JavaRDD.toRDD(ratings), matrixRank, numIter);
	}
	
	public static MatrixFactorizationModel trainImplicit(JavaRDD<Rating> ratings, int matrixRank, int numIter, double lambda, double alpha){
		return ALS.trainImplicit(JavaRDD.toRDD(ratings), matrixRank, numIter, lambda, alpha);
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

	@Override
	public int[] recommend(int userId, int numberOfResults) {
		return null;
	}
}
