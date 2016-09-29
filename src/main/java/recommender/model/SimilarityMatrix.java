package recommender.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;

import recommender.similarities.Similarity;
import algorithm.QuickSelect;

public abstract class SimilarityMatrix {

	protected Similarity similarity;
	protected double[][] similaritiesMatrix;

	public Similarity getSimilarity() {
		return similarity;
	}

	public int getNumberOfRow() {
		return similaritiesMatrix.length;
	}

	public int getNumberOfCol() {
		return similaritiesMatrix[0].length;
	}

	public double getSimilarity(int index1, int index2) {
		return similaritiesMatrix[index1][index2];
	}

	public Double[] getColumn(int columnIndex) {
		Double[] column = new Double[similaritiesMatrix.length];
		for (int i = 0; i < similaritiesMatrix.length; i++) {
			column[i] = similaritiesMatrix[i][columnIndex];
		}
		return column;
	}

	public Double[] getRow(int rowIndex) {
		return ArrayUtils.toObject(similaritiesMatrix[rowIndex]);
	}

	public List<Integer> getTopNSimilarColumnIndices(int columnIndex, int n) {
		Double[] columnData = getColumn(columnIndex);
		columnData[columnIndex] = Double.MIN_VALUE;
		return QuickSelect.selectTopN(columnData, n).stream()
				.map(pair -> pair.getFirst()).collect(Collectors.toList());
	}

	public List<Integer> getTopNSimilarRowIndices(int rowIndice, int n) {
		Double[] columnData = getRow(rowIndice);
		columnData[rowIndice] = Double.MIN_VALUE;
		return QuickSelect.selectTopN(columnData, n).stream()
				.map(pair -> pair.getFirst()).collect(Collectors.toList());
	}
}
