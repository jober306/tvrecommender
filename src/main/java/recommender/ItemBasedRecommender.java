package recommender;

import java.util.List;
import java.util.stream.Collectors;

import model.Recommendation;
import model.UserItemMatrix;
import model.similarity.NormalizedCosineSimilarity;

import org.apache.spark.mllib.linalg.Matrix;

import scala.Tuple2;
import algorithm.QuickSelect;
import data.Context;
import data.EPG;
import data.TVDataSet;
import data.TVEvent;
import data.TVProgram;

/**
 * Class that recommends items for a specific user using collaborative filtering
 * by using item similarities.
 * 
 * @author Jonathan Bergeron
 *
 */
public class ItemBasedRecommender<T extends TVProgram, U extends TVEvent>
		extends AbstractTVRecommender<T, U> {

	/**
	 * The electronic programming guide used by this recommender.
	 */
	EPG<T> epg;

	/**
	 * The tv data set on which the matrix M prime will be build.
	 */
	TVDataSet<U> tvDataset;

	/**
	 * The user item matrix.
	 */
	UserItemMatrix R;

	/**
	 * The item similarities matrix. It is a symetric matrix represented only by
	 * an upper triangular matrix.
	 */
	Matrix S;

	/**
	 * Constructor of the class that need a rating matrix to create the item
	 * similarities matrix.
	 * 
	 * @param R
	 *            The user item matrix.
	 */
	public ItemBasedRecommender(Context<T, U> context) {
		super(context);
	}

	/**
	 * Method that train the space alignment recommender using the whole data
	 * set.
	 */
	public void train() {
		this.R = context.getTrainingSet().convertToDistUserItemMatrix();
		this.S = R.getItemSimilarities(NormalizedCosineSimilarity.getInstance());
	}

	/**
	 * Method that returns the neighborhood of an item. It returns the top n
	 * item indices and values in decreasing order.
	 * 
	 * @param itemIndex
	 *            The item index from which we calculate its neighborhood.
	 * @param n
	 *            The size of the neighborhoods set.
	 * @return A list of pair containing respectively the item index in the user
	 *         item matrix and the similarity value.
	 */
	public List<Tuple2<Integer, Double>> predictItemNeighbourhood(
			int itemIndex, int n) {
		Double[] values = new Double[S.numRows()];
		for(int row = 0; row < S.numRows(); row++){
			values[row] = S.apply(row, itemIndex);
		}
		return QuickSelect.selectTopN(values, n);
	}

	/**
	 * Method that returns the neighborhood of a new item for a specific user.
	 * It returns the top n item indices and values in decreasing order.
	 * 
	 * @param coldStartItemContent
	 *            The content of the new item.
	 * @param userIndex
	 *            The index of the user (the neighborhood returned will only
	 *            contains items seen by this user).
	 * @param n
	 *            The size of the neighborhoods set.
	 * @return A list of pair containing respectively the item index in the user
	 *         item matrix and the similarity value.
	 */
	public List<Tuple2<Integer, Double>> predictItemNeighbourhoodForUser(
			int userIndex, int itemIndex, int n) {
		List<Integer> itemsSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		List<Tuple2<Integer, Double>> itemsNeighbourhood = predictItemNeighbourhood(itemIndex, n);
		List<Tuple2<Integer, Double>> itemsNeighbourhoodForUser = itemsNeighbourhood.stream().filter(pair -> itemsSeenByUser.contains(pair._1())).collect(Collectors.toList());
		return itemsNeighbourhood.subList(0, Math.min(itemsNeighbourhoodForUser.size(), n));
	}

	@Override
	protected List<Recommendation> recommendNormally(int userId, int numberOfResults, List<T> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Recommendation> recommendForTesting(int userId, int numberOfResults, List<T> tvPrograms) {
		// TODO Auto-generated method stub
		return null;
	}
}