package recommender;

import static java.lang.Math.toIntExact;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static util.Comparators.scoredRecommendationComparator;
import static util.MllibUtilities.invertVector;
import static util.MllibUtilities.multiplicateByLeftDiagonalMatrix;
import static util.MllibUtilities.multiplicateByRightDiagonalMatrix;
import static util.MllibUtilities.multiplyMatrixByRightDiagonalMatrix;
import static util.MllibUtilities.scalarProduct;
import static util.MllibUtilities.toDenseLocalMatrix;
import static util.MllibUtilities.toDenseLocalVectors;
import static util.MllibUtilities.transpose;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import model.ScoredRecommendation;
import model.UserItemMatrix;
import model.similarity.NormalizedCosineSimilarity;

import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;

import data.Context;
import data.EvaluationContext;
import data.TVEvent;
import data.TVProgram;
import data.feature.FeatureExtractor;

/**
 * Class that finds the optimal mapping between item content and the item
 * similarities matrix as proposed by the following article:
 * http://www.ijcai.org/Proceedings/15/Papers/475.pdf It can then predicts the
 * similarity between already seen items and new items by using this mapping.
 * This recommender is used to alleviate cold start problem of tv
 * recommendation.
 * 
 * @author Jonathan Bergeron
 */
public class SpaceAlignmentRecommender<T extends TVProgram, U extends TVEvent>
		extends AbstractTVRecommender<T, U> {

	/**
	 * The feature extractor that will be used to extract features when training
	 * the model and whne predicting.
	 */
	FeatureExtractor<T, U> extractor;

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
	Map<T, List<Double>> newTVShowsSimilarities;
	

	/**
	 * Constructor of the <class>SpaceAlignmentPredictor</class>, it calculates
	 * the matrix Mprime with the ratings matrix and the corresponding content
	 * matrix and also takes into account the parameter r.
	 * 
	 * @param R
	 *            The rating matrix.
	 * @param r
	 *            The maximum rank the matrix Mprime can have.
	 * @param C
	 *            The content matrix of all the items.
	 */
	public SpaceAlignmentRecommender(Context<T, U> context,
			FeatureExtractor<T, U> extractor, int r, int neighbourhoddSize) {
		super(context);
		this.extractor = extractor;
		this.r = r;
		this.neighbourhoodSize = neighbourhoddSize;
	}

	public void train() {
		this.R = context.getTrainingSet().convertToLocalUserItemMatrix();
		this.C = context.getTrainingSet().getContentMatrix(extractor);
		this.localC = toDenseLocalVectors(C);
		calculateMprime();
		if (context instanceof EvaluationContext) {
			EvaluationContext<T,U> evalContext = (EvaluationContext<T,U>) context;
			this.newTVShowsSimilarities = initializeNewTVShowSimilarities(evalContext.getTestPrograms());
		}
	}
	
	private Map<T, List<Double>> initializeNewTVShowSimilarities(List<T> tvPrograms){
		Map<T, Vector> newTvShows = tvPrograms.stream().collect(toMap(Function.identity(), extractor::extractFeaturesFromProgram));
		return newTvShows.entrySet().stream().collect(toMap(Entry::getKey, entry -> calculateNewTVShowSimilarities(entry.getValue())));
	}
	
	private List<Double> calculateNewTVShowSimilarities(Vector coldStartItemContent) {
		int numberOfItems = (int) context.getEvents().getNumberOfTvShows();
		return IntStream.range(0, numberOfItems).mapToDouble(index -> calculateItemsSimilarity(coldStartItemContent, index)).boxed().collect(toList());
	}

	@Override
	protected List<ScoredRecommendation> recommendNormally(int userId, int numberOfResults, List<T> tvPrograms) {
		System.out.print("Extracting features from program...");
		Map<T, Vector> newTvShows = tvPrograms.stream().collect(toMap(Function.identity(), extractor::extractFeaturesFromProgram));
		System.out.println("Done");
		System.out.print("Finding neighbourhood for all items...");
		List<ScoredRecommendation> recommendations = newTvShows.entrySet().stream().map(entry -> scoreTVProgram(userId, entry)).collect(toList());
		System.out.println("Done");
		System.out.print("Sorting results...");
		recommendations.sort(scoredRecommendationComparator());
		System.out.println("Done");
		return recommendations.subList(0, Math.min(recommendations.size(), numberOfResults));
	}
	
	@Override
	protected List<ScoredRecommendation> recommendForTesting(int userId,
			int numberOfResults, List<T> tvProrams) {
		System.out.println("Getting neighbourhood for all items...");
		List<ScoredRecommendation> recommendations = tvProrams.stream().map(program -> scoreTVProgram(userId, program)).collect(toList());
		System.out.print("Done");
		System.out.println("Sorting results...");
		recommendations.sort(scoredRecommendationComparator());
		System.out.print("Done");
		return recommendations.subList(0, Math.min(recommendations.size(), numberOfResults));
	}
	
	private ScoredRecommendation scoreTVProgram(int userId, Entry<T, Vector> programWithFeatures) {
		T program = programWithFeatures.getKey();
		Vector programFeatures = programWithFeatures.getValue();
		return new ScoredRecommendation(program, calculateScore(userId, programFeatures));
	}
	
	private ScoredRecommendation scoreTVProgram(int userId, T tvProgram){
		return new ScoredRecommendation(tvProgram, getScore(userId, tvProgram));
	}

	private double calculateScore(int userId, Vector vector) {
		return calculateNeighboursScore(calculateNewItemNeighborhoodSimilaritiesForUser(vector, userId));
	}
	
	private double getScore(int userId, T program){
		return calculateNeighboursScore(getNewItemNeighborhoodSimilaritiesForUser(userId, program));
	}
	
	private double calculateNeighboursScore(List<Double> neighbours) {
		return neighbours.stream().reduce(0.0d, Double::sum) / (double) neighbours.size();
	}
	
	private List<Double> calculateNewItemNeighborhoodSimilaritiesForUser(Vector coldStartItemContent, int userIndex) {
		List<Integer> itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		Stream<Double> filteredSimilarities = itemIndexesSeenByUser.stream().map(i -> calculateItemsSimilarity(coldStartItemContent, i));
		List<Double> sortedFilteredSimilarities = filteredSimilarities.sorted(Comparator.<Double>naturalOrder().reversed()).collect(toList());
		return sortedFilteredSimilarities.subList(0, Math.min(neighbourhoodSize, sortedFilteredSimilarities.size()));
	}
	
	private List<Double> getNewItemNeighborhoodSimilaritiesForUser(int userId, T tvShow){
		List<Integer> itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userId);
		//TODO:CHECK IF TVPROGRAM HAS HASH METHOD AND EQUALS.
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
		SingularValueDecomposition<IndexedRowMatrix, Matrix> Csvd = C
				.computeSVD(toIntExact(C.numCols()), true, 0.0d);
		IndexedRowMatrix U = Csvd.U();
		Matrix V = Csvd.V();
		Vector sigma = Csvd.s();
		Vector invertedSigma = invertVector(sigma);
		IndexedRowMatrix Ut = transpose(U);
		// *********************************Intermediate//
		// operations*********************************************
		IndexedRowMatrix leftMat = multiplicateByLeftDiagonalMatrix(
				invertedSigma, Ut);
		IndexedRowMatrix rightMat = multiplicateByRightDiagonalMatrix(U,
				Csvd.s());
		Matrix localRightMat = R.getItemSimilarities(
				NormalizedCosineSimilarity.getInstance()).multiply(
				toDenseLocalMatrix(rightMat));
		IndexedRowMatrix intermediateMat = leftMat.multiply(localRightMat);
		// ***************************************************************************************************
		SingularValueDecomposition<IndexedRowMatrix, Matrix> intMatsvd = intermediateMat
				.computeSVD(r, true, 0.0d);
		Matrix Q = intMatsvd.V();
		DenseMatrix VQ = V.multiply((DenseMatrix) Matrices.dense(Q.numRows(),
				Q.numCols(), Q.toArray()));
		DenseMatrix QtVt = VQ.transpose();
		Vector hardTrhesholdedLambda = intMatsvd.s();
		Mprime = multiplyMatrixByRightDiagonalMatrix(VQ, hardTrhesholdedLambda)
				.multiply(QtVt);
	}
}
