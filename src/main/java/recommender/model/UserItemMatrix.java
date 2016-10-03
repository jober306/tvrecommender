package recommender.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;

import recommender.similarities.Similarity;

/**
 * Class that models a user-item matrix.
 * 
 * @author Jonathan Bergeron
 *
 */
public class UserItemMatrix {

	/**
	 * Implicit value given when an user has seen a tv show but no rating.
	 */
	public static final int USER_SEEN_ITEM_VALUE = 1;

	/**
	 * Implicit value given when an user hasnt seen a tv show.
	 */
	public static final int USER_NOT_SEEN_ITEM_VALUE = 0;

	/**
	 * The user itme matrix represented by double entries.
	 */
	double[][] userItemMatrix;

	/**
	 * Constructor of the class. Matrix is initialized with zeros.
	 * 
	 * @param numberOfUsers
	 *            The number of distinct users.
	 * @param numberOfItems
	 *            The number of distinct items.
	 */
	public UserItemMatrix(int numberOfUsers, int numberOfItems) {
		userItemMatrix = new double[numberOfUsers][numberOfItems];
	}

	/**
	 * Constructor of the class initialized with the given data.
	 * 
	 * @param data
	 *            The data with wich the userItemMatrix will be initialized.
	 */
	public UserItemMatrix(double[][] data) {
		userItemMatrix = new double[data.length][data[0].length];
		for (int row = 0; row < data.length; row++) {
			for (int col = 0; col < data[0].length; col++) {
				userItemMatrix[row][col] = data[row][col];
			}
		}
	}

	/**
	 * Getter method that returns the number of users.
	 * 
	 * @return
	 */
	public int getNumberOfUsers() {
		return userItemMatrix.length;
	}

	/**
	 * Getter method that returns the number of items.
	 * 
	 * @return
	 */
	public int getNumberOfItems() {
		// Assuming there is at least one user in the User-Item matrix.
		return userItemMatrix[0].length;
	}

	/**
	 * Getter method that returns the value given by a user for a specific item.
	 * 
	 * @param userID
	 *            The user id.
	 * @param itemID
	 *            The item id.
	 * @return The rating given by the user for the item.
	 */
	public double getRating(int userID, int itemID) {
		return userItemMatrix[userID][itemID];
	}

	/**
	 * Getter method that return all the value of the specified item.
	 * 
	 * @param itemIndex
	 *            the item index.
	 * @return All the values given to this item.
	 */
	public double[] getItemValues(int itemIndex) {
		double[] itemValues = new double[userItemMatrix.length];
		for (int i = 0; i < itemValues.length; i++) {
			itemValues[i] = userItemMatrix[i][itemIndex];
		}
		return itemValues;
	}

	/**
	 * Getter method that return all the value given by the specified user.
	 * 
	 * @param userIndex
	 *            the user index.
	 * @return All the values given by this user.
	 */
	public double[] getUserValues(int userIndex) {
		return userItemMatrix[userIndex];
	}

	/**
	 * Getter method that returns all the data in the matrix in major column
	 * format.
	 * 
	 * @return The data contained in the user-item matrix in major column
	 *         format.
	 */
	public double[] getDataMajorColumn() {
		int numberOfUsers = getNumberOfUsers();
		int numberOfItems = getNumberOfItems();
		double[] matrixData = new double[numberOfUsers * numberOfItems];
		for (int col = 0; col < numberOfItems; col++) {
			for (int row = 0; row < numberOfUsers; row++) {
				matrixData[col * numberOfUsers + row] = userItemMatrix[row][col];
			}
		}
		return matrixData;
	}

	/**
	 * Getter method that returns all the data in the matrix in major row
	 * format.
	 * 
	 * @return The data contained in the user-item matrix in major row format.
	 */
	public double[] getDataMajorRow() {
		int numberOfUsers = getNumberOfUsers();
		int numberOfItems = getNumberOfItems();
		double[] matrixData = new double[numberOfUsers * numberOfItems];
		for (int row = 0; row < numberOfUsers; row++) {
			for (int col = 0; col < numberOfItems; col++) {
				matrixData[row * numberOfItems + col] = userItemMatrix[row][col];
			}
		}
		return matrixData;
	}

	/**
	 * Getter method that returns all the users in the mllib format.
	 * 
	 * @return The list of all users in mllib format
	 *         (<class>IndexedRow</class>).
	 */
	public List<IndexedRow> getUsersAsVector() {
		List<IndexedRow> rows = new ArrayList<IndexedRow>();
		for (int row = 0; row < getNumberOfUsers(); row++) {
			Vector vectorRow = Vectors.dense(getUserValues(row));
			rows.add(new IndexedRow(row, vectorRow));
		}
		return rows;
	}

	/**
	 * Getter method that returns all the items in the mllib format.
	 * 
	 * @return The list of all items in mllib format
	 *         (<class>IndexedRow</class>).
	 */
	public List<IndexedRow> getItemsAsVector() {
		List<IndexedRow> columns = new ArrayList<IndexedRow>();
		for (int col = 0; col < getNumberOfItems(); col++) {
			Vector vectorCol = Vectors.dense(getItemValues(col));
			columns.add(new IndexedRow(col, vectorCol));
		}
		return columns;
	}

	/**
	 * Setter method that set the default user seen item value for the given
	 * user and item.
	 * 
	 * @param userID
	 *            The user id.
	 * @param itemID
	 *            The item id.
	 */
	public void setUserSeenItem(int userID, int itemID) {
		userItemMatrix[userID][itemID] = USER_SEEN_ITEM_VALUE;
	}

	/**
	 * Setter method that set the default user not seen item value for the given
	 * user and item.
	 * 
	 * @param userID
	 *            The user id.
	 * @param itemID
	 *            The item id.
	 */
	public void setUserNotSeenItem(int userID, int itemID) {
		userItemMatrix[userID][itemID] = USER_NOT_SEEN_ITEM_VALUE;
	}

	/**
	 * Setter method that set the specified value for the given user and item.
	 * 
	 * @param userID
	 *            The user id.
	 * @param itemID
	 *            The item id.
	 * @param value
	 *            The value given by the user for the item.
	 */
	public void setUserItemValue(int userID, int itemID, double value) {
		userItemMatrix[userID][itemID] = value;
	}

	/**
	 * Setter method that set the values of all item for the given user.
	 * 
	 * @param userID
	 *            The user id.
	 * @param values
	 *            The values given to all items by the user. Must be of length
	 *            #of items.
	 */
	public void setUserValues(int userID, double[] values) {
		for (int i = 0; i < values.length; i++) {
			userItemMatrix[userID][i] = values[i];
		}
	}

	/**
	 * Setter method that set the values by all users for the given item.
	 * 
	 * @param itemID
	 *            The item id.
	 * @param values
	 *            The values given by all users to the item. Must be of length
	 *            #of users.
	 */
	public void setItemValues(int itemID, double[] values) {
		for (int i = 0; i < values.length; i++) {
			userItemMatrix[i][itemID] = values[i];
		}
	}

	/**
	 * Method that calculate a similarity between two users.
	 * 
	 * @param userIndex1
	 *            The first user.
	 * @param userIndex2
	 *            The second user.
	 * @param similarity
	 *            The similarity to be used between the two users.
	 * @return The similarity between the two users.
	 */
	public double getUsersSimilarity(int userIndex1, int userIndex2,
			Similarity similarity) {
		return similarity.calculateSimilarity(getUserValues(userIndex1),
				getUserValues(userIndex2));
	}

	/**
	 * Method that calculate a similarity between two items.
	 * 
	 * @param userIndex1
	 *            The first item.
	 * @param userIndex2
	 *            The second item.
	 * @param similarity
	 *            The similarity to be used between the two items.
	 * @return The similarity between the two items.
	 */
	public double getItemsSimilarity(int itemIndex1, int itemIndex2,
			Similarity similarity) {
		return similarity.calculateSimilarity(getItemValues(itemIndex1),
				getItemValues(itemIndex2));
	}
	
	/**
	 * Method that extract for each user all the item index with non-zero entry.
	 * This set is often called omega in the litterature.
	 * @return The map that for each user gives all the item already rated.
	 */
	public HashMap<Integer, List<Integer>> getItemIndexesSeenByUsers(){
		HashMap<Integer, List<Integer>> omega = new HashMap<Integer, List<Integer>>();
		for(int user = 0; user < getNumberOfUsers(); user++){
			omega.put(user, new ArrayList<Integer>());
			for(int item = 0; item < getNumberOfItems(); item++){
				double rating = getRating(user, item);
				if(rating != 0){
					omega.get(user).add(item);
				}
			}
		}
		return omega;
	}

	/**
	 * Method that calculate the item similarity matrix with the given
	 * similarity.
	 * 
	 * @param similarity
	 *            The similarity to be used to calculate similarity between
	 *            items.
	 * @return An <class>ItemSimilaritiesMatrix</class> with respect to this
	 *         user-item matrix and the given similarity.
	 */
	public ItemSimilaritiesMatrix getItemSimilaritiesMatrix(
			Similarity similarity) {
		return new ItemSimilaritiesMatrix(this, similarity);
	}

	/**
	 * Method that calculate the user similarity matrix with the given
	 * similarity.
	 * 
	 * @param similarity
	 *            The similarity to be used to calculate similarity between
	 *            users.
	 * @return An <class>UserSimilaritiesMatrix</class> with respect to this
	 *         user-item matrix and the given similarity.
	 */
	public UserSimilaritiesMatrix getUserSimilaritiesMatrix(
			Similarity similarity) {
		return new UserSimilaritiesMatrix(this, similarity);
	}

	/**
	 * Method that override the to string method. It returns a string formated
	 * like a matrix with the corresponding data of this user-item matrix.
	 */
	@Override
	public String toString() {
		String str = "";
		for (int row = 0; row < userItemMatrix.length; row++) {
			for (int col = 0; col < userItemMatrix[0].length; col++) {
				str += userItemMatrix[row][col] + " ";
			}
			str += "\n";
		}
		return str;
	}

	public static void main(String[] args) {
		UserItemMatrix test = new UserItemMatrix(3, 5);
		System.out.print(test);
	}
}
