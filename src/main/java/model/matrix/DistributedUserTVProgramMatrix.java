package model.matrix;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;

import model.data.TVProgram;
import model.data.User;
import model.data.mapping.Mapping;
import util.spark.SparkUtilities;

/**
 * Class that represents an user item matrix (or rating matrix) in a mllib
 * distributed representation.
 * 
 * @author Jonathan Bergeron
 *
 */
public class DistributedUserTVProgramMatrix<U extends User, UM, P extends TVProgram, PM> extends UserTVProgramMatrix<U, UM, P, PM>{
	
	/**
	 * The mllib distributed matrix.
	 */
	IndexedRowMatrix data;

	/**
	 * Constructor of the class.
	 * 
	 */
	public DistributedUserTVProgramMatrix(JavaRDD<IndexedRow> rows, Mapping<U, UM> userMapping, Mapping<P, PM> tvProgramMapping) {
		super(userMapping, tvProgramMapping);
		data = new IndexedRowMatrix(rows.rdd());
	}

	@Override
	public double getValue(int rowIndex, int columnIndex) {
		return getRow(rowIndex).toArray()[columnIndex];
	}

	@Override
	public long getNumRows() {
		return data.numRows();
	}

	@Override
	public long getNumCols() {
		return data.numCols();
	}
	
	@Override
	public Vector getRow(int rowIndex) {
		List<IndexedRow> rows = data.rows().toJavaRDD()
				.filter(indexedRow -> indexedRow.index() == rowIndex).collect();
		if (rows.size() == 1) {
			return rows.get(0).vector();
		}
		return null;
	}
	
	@Override
	public Vector getColumn(int colIndex) {
		List<IndexedRow> rows = data.toBlockMatrix().transpose().toIndexedRowMatrix().rows().toJavaRDD()
				.filter(indexedRow -> indexedRow.index() == colIndex).collect();
		if (rows.size() == 1) {
			return rows.get(0).vector();
		}
		return null;
	}
	
	@Override
	public Map<Integer, Vector> columnVectorMap(){
		return data.toBlockMatrix()
			.transpose()
			.toIndexedRowMatrix()
			.rows().toJavaRDD().collect().stream()
			.collect(Collectors.toMap(row -> (int) row.index(), IndexedRow::vector));
	}

	public CoordinateMatrix getItemSimilarities(){
		long nCols = getNumCols();
		JavaRDD<MatrixEntry> entries = data.columnSimilarities().entries().toJavaRDD();
		JavaRDD<MatrixEntry> simEntries = entries.map(entry -> new MatrixEntry(entry.j(), entry.i(), entry.value()));
		List<MatrixEntry> diagonalEntriesList = LongStream.range(0, nCols).boxed().map(index -> new MatrixEntry(index, index, 1.0d)).collect(Collectors.toList());
		JavaRDD<MatrixEntry> diagonalEntries = SparkUtilities.elementsToJavaRDD(diagonalEntriesList, new JavaSparkContext(entries.context()));
		return new CoordinateMatrix(entries.union(simEntries).union(diagonalEntries).rdd(), nCols, nCols);
	}
}
