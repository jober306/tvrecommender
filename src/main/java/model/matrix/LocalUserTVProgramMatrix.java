package model.matrix;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;

import model.data.TVProgram;
import model.data.User;

/**
 * Class that wraps the local matrix of mllib to represent an user-item matrix.
 * It offers multiple utilities methods.
 * @author Jonathan Bergeron
 *
 */
public class LocalUserTVProgramMatrix<U extends User, P extends TVProgram> extends UserTVProgramMatrix<U, P> implements Serializable{

	private static final long serialVersionUID = 6401494865309223613L;
	
	/**
	 * The mllib user item matrix.
	 */
	Matrix R;
	
	/**
	 * Construct a dense matrix with numRow and numCol with all the specified values.
	 * @param numRow The matrix number of row.
	 * @param numCol The matrix number of column.
	 * @param values The values of the matrix in column major order.
	 */
	public LocalUserTVProgramMatrix(int numRow, int numCol, double[] values, UserTVProgramMapping<U, P> mapping){
		super(mapping);
		this.R = Matrices.dense(numRow, numCol, values);
	}
	

	/**
	 * Construct a sparse matrix with numRow and numCol with all the specified values. 
	 * The values are stored in Compressed Sparse Column (CSC) format. For example, the following matrix

									   1.0 0.0 4.0
									   0.0 3.0 5.0
									   2.0 0.0 6.0
 
	 * is stored as values: [1.0, 2.0, 3.0, 4.0, 5.0, 6.0], rowIndices=[0, 2, 1, 0, 1, 2], colPointers=[0, 2, 3, 6].
	 * @param numRow
	 * @param numCol
	 * @param colPtrs
	 * @param rowIndices
	 * @param values
	 */
	public LocalUserTVProgramMatrix(int numRow, int numCol, int[] colPtrs, int[] rowIndices, double[] values, UserTVProgramMapping<U, P> mapping){
		super(mapping);
		this.R = Matrices.sparse(numRow, numCol, colPtrs, rowIndices, values);
	}
	
	@Override
	public double getValue(int rowIndex, int columnIndex) {
		return R.apply(rowIndex, columnIndex);
	}
	
	@Override
	public Vector getRow(int rowIndex) {
		scala.collection.Iterator<Vector> rows = R.rowIter();
		int currentRow = 0;
		while(currentRow != rowIndex && rows.hasNext()){
			rows.next();
			currentRow++;
		}
		return rows.next();
	}
	

	@Override
	public Vector getColumn(int colIndex) {
		scala.collection.Iterator<Vector> cols = R.colIter();
		int currentRow = 0;
		while(currentRow != colIndex && cols.hasNext()){
			cols.next();
			currentRow++;
		}
		return cols.next();
	}
	
	@Override
	public long getNumRows() {
		return R.numRows();
	}
	
	@Override
	public long getNumCols() {
		return (int)R.numCols();
	}
	
	@Override
	public Map<Integer, Vector> columnVectorMap(){
		Map<Integer, Vector> vectors = new HashMap<Integer, Vector>();
		scala.collection.Iterator<Vector> it = R.colIter();
		int index = 0;
		while(it.hasNext()){
			vectors.put(index, it.next().toSparse());
			index++;
		}
		return vectors;
	}
}
