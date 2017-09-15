package model;

import java.io.Serializable;
import java.util.List;

import model.similarity.SimilarityMeasure;

import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;

/**
 * Abstract class that represents an user item matrix. 
 * The class 
 * @author Jonathan Bergeron
 *
 */
public abstract class UserItemMatrix implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7905292446619491537L;
	
	/**
	 * Getter Methods
	 */
	abstract public double getValue(int rowIndex, int columnIndex);
	abstract public long getNumRows();
	abstract public long getNumCols();
	
	/**
	 * Utility Methods
	 */
	abstract public Vector getRow(int rowIndex);
	abstract public List<Integer> getItemIndexesSeenByUser(int userIndex);
	abstract public Matrix getItemSimilarities(SimilarityMeasure similairty);
}
