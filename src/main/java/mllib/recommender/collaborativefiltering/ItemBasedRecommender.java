package mllib.recommender.collaborativefiltering;

import static java.lang.Math.toIntExact;

import static list.utility.ListUtilities.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mllib.model.DistributedUserItemMatrix;
import scala.Tuple2;

import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import algorithm.QuickSelect;
import data.model.TVDataSet;
import data.model.TVEvent;
import data.model.TVProgram;

/**
 * Class that recommends items for a specific user using collaborative filtering
 * by using item similarities.
 * 
 * @author Jonathan Bergeron
 *
 */
public class ItemBasedRecommender<T extends TVProgram, U extends TVEvent>{
	
	/**
	 * The tv data set on which the matrix M prime will be build.
	 */
	TVDataSet<U> tvDataset;
	
	/**
	 * The user item matrix.
	 */
	public DistributedUserItemMatrix R;

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
	public ItemBasedRecommender(TVDataSet<U> dataSet) {
		R = dataSet.convertToDistUserItemMatrix();
		S = R.getItemSimilarities();
	}

	/**
	 * Method that returns the neighborhood of an item. It
	 * returns the top n item indices and values in decreasing order.
	 * 
	 * @param itemIndex
	 *            The item index from which we calculate its neighborhood.
	 * @param n
	 *            The size of the neighborhoods set.
	 * @return A list of pair containing respectively the item index in the user
	 *         item matrix and the similarity value.
	 */
	public List<Tuple2<Integer, Double>> predictItemNeighbourhood(int itemIndex, int n) {
		List<Tuple2<Integer, Double>> indicesAndSimilarities = S.entries().toJavaRDD().filter(matrixEntry -> {
			long rowIndex = matrixEntry.i();
			long colIndex = matrixEntry.j();
			if(rowIndex != colIndex && rowIndex == itemIndex){
				return true;
			}else{
				return false;
			}
		}).map(matrixEntry -> new Tuple2<Integer,Double>(toIntExact(matrixEntry.j()), matrixEntry.value())).collect();
		int[] indices = Ints.toArray(getFirstArgument(indicesAndSimilarities));
		double[] values = Doubles.toArray(getSecondArgument(indicesAndSimilarities));
		return QuickSelect.selectTopN(indices, values, n);
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
	public List<Tuple2<Integer, Double>> predictItemNeighbourhoodForUser(int userIndex,int itemIndex, int n) {
		List<Integer> itemsSeenByUser = Arrays.asList(ArrayUtils.toObject(R.getItemIndexesSeenByUser(userIndex)));
		List<Tuple2<Integer, Double>> itemsNeighbourhood = predictItemNeighbourhood(itemIndex, n);
		List<Tuple2<Integer, Double>> itemsNeighbourhoodForUser = itemsNeighbourhood.stream().filter(pair -> itemsSeenByUser.contains(pair._1())).collect(Collectors.toList());
		return itemsNeighbourhood.subList(0, Math.min(itemsNeighbourhoodForUser.size(), n));
	}
}
