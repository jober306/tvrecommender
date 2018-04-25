package recommender;

import static java.lang.Math.toIntExact;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static util.spark.mllib.MllibUtilities.invertVector;
import static util.spark.mllib.MllibUtilities.scalarProduct;
import static util.spark.mllib.MllibUtilities.toDenseLocalVectors;
import static util.spark.mllib.MllibUtilities.vectorToCoordinateMatrix;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.BlockMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import data.Context;
import data.EvaluationContext;
import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import model.feature.FeatureExtractor;
import model.matrix.UserItemMatrix;
import model.recommendation.Recommendations;
import model.recommendation.ScoredRecommendation;
import util.spark.mllib.MllibUtilities;

/**
 * Class that finds the optimal bilinear mapping between item content and the item
 * similarities matrix as proposed by the following article:
 * http://www.ijcai.org/Proceedings/15/Papers/475.pdf It can then predicts the
 * similarity between already seen items and new items by using this mapping.
 * This recommender is used to alleviate cold start problem of tv
 * recommendation.
 * 
 * @author Jonathan Bergeron
 */
public class SpaceAlignmentRecommender<P extends TVProgram, E extends TVEvent<User, P>>
		extends TVRecommender<User, P, E, ScoredRecommendation> {
	
	/**
	 * The java spark context. It is necessary to create matrix entries in the training process.
	 */
	JavaSparkContext sc;
	
	/**
	 * The feature extractor that will be used to extract features when training
	 * the model and when predicting.
	 */
	FeatureExtractor<? super P, ? super E> extractor;

	/**
	 * The user item (or rating) matrix that represents the tv data set.
	 */
	UserItemMatrix R;

	/**
	 * The already seen item contents matrix. Suppose each item is represented
	 * by d features, then the matrix C is of size n x d.
	 */
	IndexedRowMatrix C;

	/**
	 * Local version of this indexed row matrix.
	 */
	List<Vector> localC;

	/**
	 * This parameter indicates the maximum rank the matrix Mprime can have.
	 * Note that Mprime has maximum rank d, So r should be between 1 and d. The
	 * higher r is set the better it will fit the item similarity matrix but
	 * higher the chance it will overfit it. On the other hand if it is too low
	 * it could be impossible to represent the item similarity distribution
	 * properly.
	 */
	int r;

	/**
	 * The neighbourhood size used when calculating similarity between items.
	 */
	int neighbourhoodSize;

	/**
	 * This matrix of size d x d represents the model to map a new item content
	 * and an ancient item content to their similarity in the collaborative
	 * filtering space.
	 */
	Matrix Mprime;
	
	/**
	 * Map used when evaluation context is used. It stores for each new program their similarities
	 * with respect to the space alignment algorithm with each old tv shows.
	 */
	Map<P, List<Double>> newTVShowsSimilarities;
	
	/**
	 * Constructor without a context of this class.
	 * @param numberOfRecommendations The number of recommendations that will be made when recommending.
	 * @param extractor The feature extractor used to train the recommender and transform new tv programs when recommending.
	 * @param r The rank of the learned matrix.
	 * @param neighbourhoddSize The neighbourhood size, i.e. the number of similar items used to compute recommendation score.
	 * @param sc The java spark context.
	 */
	public SpaceAlignmentRecommender(int numberOfRecommendations, FeatureExtractor<? super P, ? super E> extractor, int r, int neighbourhoddSize, JavaSparkContext sc) {
		super(numberOfRecommendations);
		this.extractor = extractor;
		this.r = r;
		this.neighbourhoodSize = neighbourhoddSize;
		this.sc = sc;
	}
	
	/**
	 * Main constructor of this class.
	 * @param context The context in which the recommender will live.
	 * @param numberOfRecommendations The number of recommendations that will be made when recommending.
	 * @param extractor The feature extractor used to train the recommender and transform new tv programs when recommending.
	 * @param r The rank of the learned matrix.
	 * @param neighbourhoddSize The neighbourhood size, i.e. the number of similar items used to compute recommendation score.
	 * @param sc The java spark context.
	 */
	public SpaceAlignmentRecommender(Context<User, P, E> context, int numberOfRecommendations,
			FeatureExtractor<? super P, ? super E> extractor, int r, int neighbourhoddSize, JavaSparkContext sc) {
		super(context, numberOfRecommendations);
		this.extractor = extractor;
		this.r = r;
		this.neighbourhoodSize = neighbourhoddSize;
		this.sc = sc;
	}

	public void train() {
		this.R = context.getTrainingSet().convertToLocalUserItemMatrix();
		this.C = context.getTrainingSet().getContentMatrix(extractor);
		this.localC = toDenseLocalVectors(C);
		calculateMprime();
		if (context instanceof EvaluationContext) {
			EvaluationContext<User, P, E> evalContext = (EvaluationContext<User, P, E>) context;
			this.newTVShowsSimilarities = initializeNewTVShowSimilarities(evalContext.getTestPrograms());
		}
	}
	
	public void printMPrime(){
		System.out.println(Mprime.toString());
	}
	
	private Map<P, List<Double>> initializeNewTVShowSimilarities(List<P> tvPrograms){
		return tvPrograms.stream().collect(toMap(Function.identity(), t -> calculateNewTVShowSimilarities(extractor.extractFeaturesFromProgram(t))));
	}
	
	private List<Double> calculateNewTVShowSimilarities(Vector coldStartItemContent) {
		int numberOfItems = (int) context.getTrainingSet().getNumberOfTvShowIndexes();
		return IntStream.range(0, numberOfItems).mapToDouble(index -> calculateItemsSimilarity(coldStartItemContent, index)).boxed().collect(toList());
	}

	@Override
	protected Recommendations<ScoredRecommendation> recommendNormally(int userId, List<P> tvPrograms) {
		Map<P, Vector> newTvShows = tvPrograms.stream().collect(toMap(Function.identity(), extractor::extractFeaturesFromProgram));
		List<ScoredRecommendation> recommendations = newTvShows.entrySet().stream()
				.map(entry -> scoreTVProgram(userId, entry))
				.sorted(Comparator.comparing(ScoredRecommendation::score).reversed())
				.limit(numberOfRecommendations)
				.collect(toList());
		return new Recommendations<>(userId, recommendations);
	}
	
	@Override
	protected Recommendations<ScoredRecommendation> recommendForTesting(int userId, List<P> tvPrograms) {
		List<Integer> itemIndexesSeenByUser = ((EvaluationContext<User, P, E>) context).getGroundTruth().get(userId).stream().map(P::programId).collect(toList());
		List<ScoredRecommendation> recommendations = tvPrograms.stream()
				.map(program -> scoreTVProgram(itemIndexesSeenByUser, program))
				.sorted(Comparator.comparing(ScoredRecommendation::score).reversed())
				.limit(numberOfRecommendations)
				.collect(toList());
		return new Recommendations<>(userId, recommendations);
	}
	
	@Override
	protected Map<String, String> additionalParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("Number of Recommendations: ", Integer.toString(numberOfRecommendations));
		parameters.put("Feature Extractor", this.extractor.getClass().getSimpleName());
		parameters.put("Maximum Rank", Integer.toString(this.r));
		parameters.put("Neighbourhood Size", Integer.toString(this.neighbourhoodSize));
		return parameters;
	}
	
	private ScoredRecommendation scoreTVProgram(int userId, Entry<P, Vector> programWithFeatures) {
		P program = programWithFeatures.getKey();
		Vector programFeatures = programWithFeatures.getValue();
		return new ScoredRecommendation(program, calculateScore(userId, programFeatures));
	}
	
	private ScoredRecommendation scoreTVProgram(List<Integer> itemIndexesSeenByUser, P tvProgram){
		return new ScoredRecommendation(tvProgram, getScore(itemIndexesSeenByUser, tvProgram));
	}

	private double calculateScore(int userId, Vector vector) {
		return calculateNeighboursScore(calculateNewItemNeighborhoodSimilaritiesForUser(vector, userId));
	}
	
	private double getScore(List<Integer> itemIndexesSeenByUser, P program){
		return calculateNeighboursScore(getNewItemNeighborhoodSimilaritiesForUser(itemIndexesSeenByUser, program));
	}
	
	private double calculateNeighboursScore(List<Double> neighbours) {
		if(neighbours.size() == 0){
			return 0.0d;
		}
		return neighbours.stream().reduce(0.0d, Double::sum) / (double) neighbours.size();
	}
	
	private List<Double> calculateNewItemNeighborhoodSimilaritiesForUser(Vector coldStartItemContent, int userIndex) {
		List<Integer> itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		Stream<Double> filteredSimilarities = itemIndexesSeenByUser.stream().map(i -> calculateItemsSimilarity(coldStartItemContent, i));
		List<Double> sortedFilteredSimilarities = filteredSimilarities.sorted(Comparator.<Double>naturalOrder().reversed()).collect(toList());
		return sortedFilteredSimilarities.subList(0, Math.min(neighbourhoodSize, sortedFilteredSimilarities.size()));
	}
	
	private List<Double> getNewItemNeighborhoodSimilaritiesForUser(List<Integer> itemIndexesSeenByUser, P tvShow){
		List<Double> similarities = newTVShowsSimilarities.get(tvShow);
		Stream<Double> filteredSimilarities = itemIndexesSeenByUser.stream().map(similarities::get);
		List<Double> sorteredFilteredSimilarities = filteredSimilarities.sorted(Comparator.<Double>naturalOrder().reversed()).collect(toList());
		return sorteredFilteredSimilarities.subList(0, Math.min(neighbourhoodSize, sorteredFilteredSimilarities.size()));
	}
	
	private double calculateItemsSimilarity(Vector coldStartItemContent,
			int oldItemIndex) {
		Vector targetItem = localC.get(oldItemIndex);
		return scalarProduct(Mprime.multiply(coldStartItemContent), targetItem);
	}

	private void calculateMprime() {
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd = C.computeSVD(toIntExact(C.numCols()), true, 0.0d);
		BlockMatrix U = Csvd.U().toBlockMatrix();
		DenseMatrix V = (DenseMatrix)Csvd.V();
		BlockMatrix invertedSigma = vectorToCoordinateMatrix(invertVector(Csvd.s()), sc).toBlockMatrix();
		BlockMatrix Ut = U.transpose();
		BlockMatrix leftMat = invertedSigma.multiply(Ut);
		BlockMatrix rightMat = U.multiply(invertedSigma);
		BlockMatrix S = context.getTrainingSet().convertToDistUserItemMatrix().getItemSimilarities(sc).toBlockMatrix();
		IndexedRowMatrix intermediateMat = leftMat.multiply(S).multiply(rightMat).toIndexedRowMatrix();		
		SingularValueDecomposition<IndexedRowMatrix, Matrix> intMatsvd = intermediateMat.computeSVD(r, false, 0.0d);
		//Casting the V matrix of svd to dense matrix because Spark 2.2.0 always return a dense matrix and it is needed to do matrix multiplication.
		DenseMatrix Q = (DenseMatrix) intMatsvd.V();
		Matrix lambda = MllibUtilities.vectorToDenseMatrix(intMatsvd.s());
		DenseMatrix hardThresholdedIntMat = Q.multiply(lambda.multiply(Q.transpose()));
		Mprime = V.multiply(hardThresholdedIntMat.multiply(V.transpose()));
	}
}
