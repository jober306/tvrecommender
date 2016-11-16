package mllib.recommender.collaborativefiltering;

import static java.lang.Math.toIntExact;

import java.util.Arrays;
import java.util.List;

import mllib.model.DistributedUserItemMatrix;
import mllib.utility.MllibUtilities;

import org.apache.commons.math3.util.Pair;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;

import algorithm.QuickSelect;
import ch.epfl.lamp.fjbg.JConstantPool.Entry;

/**
 * Class that recommends items for a specific user using collaborative filtering
 * by using item similarities.
 * 
 * @author Jonathan Bergeron
 *
 */
public class ItemBasedRecommender {

	/**
	 * The user item matrix.
	 */
	DistributedUserItemMatrix R;

	/**
	 * The item similarities matrix. It is a symetric matrix represented only by
	 * an upper triangular matrix.
	 */
	CoordinateMatrix S;

	/**
	 * Constructor of the class that need a rating matrix to create the item
	 * similarities matrix.
	 * 
	 * @param R
	 *            The user item matrix.
	 */
	public ItemBasedRecommender(DistributedUserItemMatrix R) {
		this.R = R;
		S = R.getItemSimilarities();
	}

	/**
	 * Method that returns the neighborhood of an item for a specific user. It
	 * returns the top n item indices and values in decreasing order.
	 * 
	 * @param userIndex
	 *            The index of the user (the neighborhood returned will only
	 *            contains items seen by this user).
	 * @param itemIndex
	 *            The item index from which we calculate its neighborhood.
	 * @param n
	 *            The size of the neighborhoods set.
	 * @return A list of pair containing respectively the item index in the user
	 *         item matrix and the similarity value.
	 */
	public List<Pair<Integer, Double>> getItemNeighborhoodForUser(
			int userIndex, int itemIndex, int n) {
		int[] itemIndexesSeenByUser = R.getItemIndexesSeenByUser(userIndex);
		int[] targetItemIndex = new int[itemIndexesSeenByUser.length];
		Arrays.fill(targetItemIndex, itemIndex);
		MllibUtilities.convertToUpperTriangularMatrixIndices(
				itemIndexesSeenByUser, targetItemIndex);
		List<MatrixEntry> entries = S
				.entries()
				.toJavaRDD()
				.filter(entry -> {
					int entryRow = toIntExact(entry.i());
					int entryCol = toIntExact(entry.j());
					return MllibUtilities.entryContainedInListOfEntries(
							entryRow, entryCol, itemIndexesSeenByUser,
							targetItemIndex);
				}).collect();
		int[] indices = new int[entries.size()];
		double[] values = new double[entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			MatrixEntry entry = entries.get(i);
			int rowIndex = toIntExact(entry.i());
			indices[i] = rowIndex == itemIndex ? toIntExact(entry.j())
					: rowIndex;
			values[i] = entry.value();
		}
		return QuickSelect.selectTopN(indices, values,
				Math.min(n, entries.size()));
	}
}
