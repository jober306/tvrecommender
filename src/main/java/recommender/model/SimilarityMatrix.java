package recommender.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;

import recommender.model.linalg.SparseVector;
import recommender.similarities.Similarity;
import scala.Tuple2;
import algorithm.QuickSelect;

/**
 * Abstract class that represents a similarity matrix.
 * 
 * @author Jonathan Bergeron
 *
 */
public abstract class SimilarityMatrix {

	/**
	 * The similarity used to generate matrix entries.
	 */
	protected Similarity similarity;

	/**
	 * The matrix data in sparse vector representation.
	 */
	protected SparseVector[] similaritiesMatrix;

	/**
	 * Getter method that returns the similarity used to calculate matrix
	 * entries.
	 * 
	 * @return The <class>Similarity</class> that was used.
	 */
	public Similarity getSimilarity() {
		return similarity;
	}

	/**
	 * Getter method that returns the number of row of this similarity matrix.
	 * 
	 * @return The number of row.
	 */
	public int getNumberOfRow() {
		return similaritiesMatrix.length;
	}

	/**
	 * Getter method that returns the number of column of this similarity
	 * matrix.
	 * 
	 * @return The number of column.
	 */
	public int getNumberOfCol() {
		return similaritiesMatrix[0].getLength();
	}

	/**
	 * Getter method that returns the similarity value between index1 and
	 * index2.
	 * 
	 * @param index1
	 *            The row index.
	 * @param index2
	 *            The col index.
	 * @return The similarity calculated at row and col coordinate.
	 */
	public double getSimilarityValue(int index1, int index2) {
		return similaritiesMatrix[index1].getValue(index2);
	}

	/**
	 * Method that returns all the similarities calculated at the given column.
	 * 
	 * @param columnIndex
	 *            The column index.
	 * @return An array of double containing all the similarities of the given
	 *         column.
	 */
	public double[] getColumn(int columnIndex) {
		double[] column = new double[similaritiesMatrix.length];
		for (int i = 0; i < similaritiesMatrix.length; i++) {
			column[i] = similaritiesMatrix[i].getValue(columnIndex);
		}
		return column;
	}

	/**
	 * Method that returns all the similarities calculated at the given row.
	 * 
	 * @param rowIndex
	 *            The row index.
	 * @return An array of double containing all the similarities of the given
	 *         row.
	 */
	public double[] getRow(int rowIndex) {
		return similaritiesMatrix[rowIndex].getDenseRepresentation();
	}

	/**
	 * Method that returns the position of the top n most similar entries for
	 * the specified column. The entry at rowIndex=columnIndex is ignored
	 * because it represents the same entity.
	 * 
	 * @param columnIndex
	 *            The column index.
	 * @param n
	 *            The number of entries to find.
	 * @return The list of position in decreasing order of the most similar
	 *         entries.
	 */
	public List<Integer> getTopNSimilarColumnIndices(int columnIndex, int n) {
		Double[] columnData = ArrayUtils.toObject(getColumn(columnIndex));
		columnData[columnIndex] = Double.MIN_VALUE;
		return QuickSelect.selectTopN(columnData, n).stream()
				.map(Tuple2::_1).collect(Collectors.toList());
	}

	/**
	 * Method that returns the position of the top n most similar entries for
	 * the specified row. The entry at columnIndex=rowIndex is ignored because
	 * it represents the same entity.
	 * 
	 * @param rowIndex
	 *            The row index.
	 * @param n
	 *            The number of entries to find.
	 * @return The list of position in decreasing order of the most similar
	 *         entries.
	 */
	public List<Integer> getTopNSimilarRowIndices(int rowIndex, int n) {
		Double[] columnData = ArrayUtils.toObject(getRow(rowIndex));
		columnData[rowIndex] = Double.MIN_VALUE;
		return QuickSelect.selectTopN(columnData, n).stream()
				.map(Tuple2::_1).collect(Collectors.toList());
	}
}
