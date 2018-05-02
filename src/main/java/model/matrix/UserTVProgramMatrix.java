package model.matrix;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import model.data.TVProgram;
import model.data.User;
import model.data.mapping.Mapping;
import model.measure.Measure;
import scala.Tuple3;
import util.spark.mllib.MllibUtilities;

import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;

/**
 * Abstract class that represents an user item matrix. 
 * The class 
 * @author Jonathan Bergeron
 *
 */
public abstract class UserTVProgramMatrix<U extends User, UM, P extends TVProgram, PM> implements Serializable{
	
	private static final long serialVersionUID = -7905292446619491537L;
	
	/**
	 * Method that returns the value at specified row index and column index.
	 * 
	 * @param rowIndex
	 *            The row index in the matrix from 0 to numRow-1.
	 * @param columnIndex
	 *            The col index in the matrix from 0 to numCol -1.
	 * @return The value at the specified row index and column index.
	 */
	abstract public double getValue(int rowIndex, int columnIndex);
	
	/**
	 * Method that returns the row of the specified index
	 * 
	 * @param rowIndex
	 *            The row index in the matrix from 0 to numRow-1.
	 * @return The Indexed Row corresponding to rowIndex.
	 */
	abstract public Vector getRow(int rowIndex);
	
	/**
	 * Method that returns the column of the specified index
	 * @param colIndex The column index
	 * @return The vector corresponding to column index in this matrix.
	 */
	abstract public Vector getColumn(int colIndex);
	
	/**
	 * Method that returns the number of rows in the user item matrix.
	 * 
	 * @return The number of rows.
	 */
	abstract public long getNumRows();
	
	/**
	 * Method that returns the number of cols in the user item matrix.
	 * 
	 * @return The number of cols.
	 */
	abstract public long getNumCols();
	
	abstract public Map<Integer, Vector> columnVectorMap();
	
	/**
	 * The mapping between user/row and tv program/column.
	 */
	protected final Mapping<U, UM> userMapping;
	protected final Mapping<P, PM> tvProgramMapping;
	
	/**
	 * Super constructor accepting the mapping from user/tv program to their respective row/column.
	 * @param mapping
	 */
	public UserTVProgramMatrix(Mapping<U, UM> userMapping, Mapping<P, PM> tvProgramMapping){
		this.userMapping = userMapping;
		this.tvProgramMapping = tvProgramMapping;
	}
	
	/**
	 * Method that return the value attached to a given user and a given program
	 * @param user The user
	 * @param tvProgram The tv program
	 * @return The value associated with this user and this tv program.
	 */
	public double getValue(U user, P tvProgram){
		return getValue(userMapping.valueToIndex(user), tvProgramMapping.valueToIndex(tvProgram));
	}
	
	/**
	 * Method that return the set containing all tv program indexes seen by user.
	 * @param user The user.
	 * @return The set of tv program indexes seen by given user.
	 */
	public Set<Integer> getTVProgramIndexesSeenByUser(U user){
		return IntStream.of(getUserRow(user).toSparse().indices()).boxed().collect(Collectors.toSet());
	}
	
	/**
	 * Method that returns the tv programs seen by the given user.
	 * @param user The user.
	 * @return The set of tv programs seen by the given user.
	 */
	public Set<PM> getTVProgramSeenByUser(U user){
		return getTVProgramIndexesSeenByUser(user).stream().map(tvProgramMapping::indexToMappedValue).collect(Collectors.toSet());
	}
	
	/**
	 * Method that return the set of user indexes that have seen the given tv program.
	 * @param program The tv program.
	 * @return The set of user indexes that have seen the given tv program.
	 */
	public Set<Integer> getUserIndexesThatHavenSeenTVProgram(P program){
		return IntStream.of(getTVProgramColumn(program).toSparse().indices()).boxed().collect(Collectors.toSet());
	}
	
	/**
	 * Method that return the set of users that have seen the given tv program.
	 * @param program The tv program.
	 * @return The set of users that have seen the given tv program.
	 */
	public Set<UM> getUsersThatHaveSeenTVProgram(P program){
		return getUserIndexesThatHavenSeenTVProgram(program).stream().map(userMapping::indexToMappedValue).collect(Collectors.toSet());
	}
	
	/**
	 * Method that return the row corresponding to given user.
	 * @param user The user.
	 * @return The row represented as a vector corresponding to the given user.
	 */
	public Vector getUserRow(U user){
		return getRow(userMapping.valueToIndex(user));
	}
	
	/**
	 * Method that return the column corresponding to given tv program.
	 * @param tvProgram The tv program.
	 * @return The column represented as a vector corresponding to the given tv program.
	 */
	public Vector getTVProgramColumn(P tvProgram){
		return getColumn(tvProgramMapping.valueToIndex(tvProgram));
	}
	
	/**
	 * Method that return cosine similarity between columns.
	 * 
	 * @param simMeasure The similarity measure used to calculate similarity between items (tv shows).
	 * 
	 * @return A sparse matrix containing the similarity between each item.
	 */
	public Matrix getItemSimilarities(Measure simMeasure) {
		final long numCols = getNumCols();
		List<MatrixEntry> entries = createSparseSimilarityMatrixEntries(numCols, simMeasure);
		Tuple3<int[], int[], double[]> matrixData = MllibUtilities.sparseMatrixFormatToCSCMatrixFormat((int) numCols, entries);
		return Matrices.sparse((int)getNumCols(), (int)getNumCols(), matrixData._1(), matrixData._2(), matrixData._3());
	}
	
	private List<MatrixEntry> createSparseSimilarityMatrixEntries(long numCols, Measure simMeasure){
		final Map<Integer, Vector> columnVectorMap = columnVectorMap();
		return IntStream.range(0, (int)numCols).boxed().flatMap(colIndex1 -> {
			return IntStream.range(colIndex1, (int)numCols).boxed().flatMap(colIndex2 -> {
				if(colIndex1 == colIndex2){
					return Stream.of(new MatrixEntry(colIndex1, colIndex2, 1.0d));
				}
				double sim = simMeasure.calculate(columnVectorMap.get(colIndex1), columnVectorMap.get(colIndex2));
				if(sim != 0){
					MatrixEntry entry = new MatrixEntry(colIndex1, colIndex2, sim);
					MatrixEntry symmetricEntry = new MatrixEntry(colIndex2, colIndex1, sim);
					return Stream.of(entry, symmetricEntry);
				}else{
					return Stream.empty();
				}
			});
		}).collect(Collectors.toList());
	}
}
