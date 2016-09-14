package recommender.model;

import recommender.model.similarityMatrix.ItemSimilaritiesMatrix;
import recommender.model.similarityMatrix.UserSimilaritiesMatrix;
import recommender.similarities.Similarity;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;

public class UserItemMatrix {
	
	public static final int USER_SEEN_ITEM_VALUE = 1;
	public static final int USER_NOT_SEEN_ITEM_VALUE = 0;
	
	double[][] userItemMatrix;
	
	public UserItemMatrix(int numberOfUsers, int numberOfItems){
		userItemMatrix = new double[numberOfUsers][numberOfItems];
	}
	
	public int getNumberOfUsers(){
		return userItemMatrix.length;
	}
	
	public int getNumberOfItems(){
		//Assuming there is at least one user in the User-Item matrix.
		return userItemMatrix[0].length;
	}
	
	public double getRating(int userID, int itemID){
		return userItemMatrix[userID][itemID];
	}
	
	public void setUserSeenItem(int userID, int itemID){
		userItemMatrix[userID][itemID] = USER_SEEN_ITEM_VALUE;
	}
	
	public void setUserNotSeenItem(int userID, int itemID){
		userItemMatrix[userID][itemID] = USER_NOT_SEEN_ITEM_VALUE;
	}
	
	public void setUserItemValue(int userID, int itemID, double value){
		userItemMatrix[userID][itemID] = value;
	}
	
	public void setUserValues(int userID, double[] values){
		for(int i = 0; i < values.length; i++){
			userItemMatrix[userID][i] = values[i];
		}
	}
	
	public void setItemValues(int itemID, double[] values){
		for(int i = 0; i < values.length; i++){
			userItemMatrix[i][itemID] = values[i];
		}
	}
	
	public double getUsersSimilarity(int userIndex1, int userIndex2, Similarity similarity){
		return similarity.calculateSimilarity(getUserValues(userIndex1), getUserValues(userIndex2));
	}
	
	public double getItemsSimilarity(int itemIndex1, int itemIndex2, Similarity similarity){
		return similarity.calculateSimilarity(getItemValues(itemIndex1), getItemValues(itemIndex2));
	}
	
	public ItemSimilaritiesMatrix getItemSimilaritiesMatrix(Similarity similarity){
		return new ItemSimilaritiesMatrix(this, similarity);
	}
	
	public UserSimilaritiesMatrix getUserSimilaritiesMatrix(Similarity similarity){
		return new UserSimilaritiesMatrix(this, similarity);
	}
	
	public double[] getItemValues(int itemIndex){
		double[] itemValues = new double[userItemMatrix.length];
		for(int i = 0; i < itemValues.length; i++){
			itemValues[i] = userItemMatrix[i][itemIndex];
		}
		return itemValues;
	}
	
	public double[] getUserValues(int userIndex){
		return userItemMatrix[userIndex];
	}
	
	public double[] getMatrixDataMajorColumn(){
		int numberOfUsers = getNumberOfUsers();
		int numberOfItems = getNumberOfItems();
		double[] matrixData = new double[numberOfUsers*numberOfItems];
		for(int col = 0; col < numberOfItems; col++){
			for(int row = 0;  row< numberOfUsers; row++){
				matrixData[col * numberOfUsers + row] = userItemMatrix[row][col];
			}
		}
		return matrixData;
	}
	
	public double[] getMatrixDataMajorRow(){
		int numberOfUsers = getNumberOfUsers();
		int numberOfItems = getNumberOfItems();
		double[] matrixData = new double[numberOfUsers*numberOfItems];
		for(int row = 0; row < numberOfItems; row++){
			for(int col = 0;  col < numberOfUsers; col++){
				matrixData[row * numberOfItems + col] = userItemMatrix[row][col];
			}
		}
		return matrixData;
	}
	
	public List<IndexedRow> getMatrixRowsAsVector(){
		List<IndexedRow> rows = new ArrayList<IndexedRow>();
		for(int row = 0; row < getNumberOfUsers(); row++){
			Vector vectorRow = Vectors.dense(getUserValues(row));
			rows.add(new IndexedRow(row, vectorRow));
		}
		return rows;
	}
	
	public List<IndexedRow> getMatrixColumnAsVector(){
		List<IndexedRow> columns = new ArrayList<IndexedRow>();
		for(int col = 0; col < getNumberOfItems(); col++){
			Vector vectorCol = Vectors.dense(getItemValues(col));
			columns.add(new IndexedRow(col, vectorCol));
		}
		return columns;
	}
	
	@Override
	public String toString(){
		String str = "";
		for(int row = 0; row < userItemMatrix.length; row++){
			for(int col = 0; col < userItemMatrix[0].length; col++){
				str += userItemMatrix[row][col] + " ";
			}
			str += "\n";
		}
		return str;
	}
	
	public static void main(String[] args){
		UserItemMatrix test = new UserItemMatrix(3, 5);
		System.out.print(test);
	}
}
