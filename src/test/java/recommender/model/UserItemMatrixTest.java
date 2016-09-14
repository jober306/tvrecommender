package recommender.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import recommender.similarities.Similarity;

public class UserItemMatrixTest {
	
	private static final int NUMBER_OF_USERS = 10;
	private static final int NUMBER_OF_ITEMS = 5;
	UserItemMatrix X;
	
	@Before
	public void setUp(){
		X = new UserItemMatrix(NUMBER_OF_USERS, NUMBER_OF_ITEMS);
	}
	
	@Test
	public void testConstructor(){
		assertEquals(NUMBER_OF_USERS, X.getNumberOfUsers());
		assertEquals(NUMBER_OF_ITEMS, X.getNumberOfItems());
	}
	
	@Test
	public void testEntriesInitializedToZero(){
		for(int user = 0; user < NUMBER_OF_USERS; user++){
			for(int item = 0; item < NUMBER_OF_ITEMS; item++){
				assertTrue(0 == X.getRating(user, item));
			}
		}
	}
	
	@Test
	public void testSetMethods(){
		double expectedValue = 2.5d;
		int testedUser = 1;
		int testedItem = 2;
		X.setUserItemValue(testedUser, testedItem,expectedValue);
		assertTrue(expectedValue == X.getRating(testedUser, testedItem));
		
		X.setUserSeenItem(testedUser, testedItem);
		assertTrue(UserItemMatrix.USER_SEEN_ITEM_VALUE == X.getRating(testedUser, testedItem));
		
		X.setUserNotSeenItem(testedUser, testedItem);
		assertTrue(UserItemMatrix.USER_NOT_SEEN_ITEM_VALUE == X.getRating(testedUser, testedItem));
		
		double[] userValues = {1,2,3,4,5};
		X.setUserValues(testedUser, userValues);
		for(int i = 0; i < NUMBER_OF_ITEMS; i++){
			assertTrue(X.getRating(testedUser, i) == userValues[i]);
		}
		
		double[] itemValues = {1,2,3,4,5,6,7,8,9,10};
		X.setItemValues(testedItem, itemValues);
		for(int i =0; i < NUMBER_OF_USERS; i++){
			assertTrue(X.getRating(i, testedItem) == itemValues[i]);
		}
	}
	
	@Test
	public void testGetUsersSimilarity(){
		Similarity sim = new Similarity(){

			@Override
			public double calculateSimilarity(double[] vector1, double[] vector2) {
				double sum = 0.0d;
				for(int i = 0; i < vector1.length; i++){
					sum += vector1[i];
					sum += vector2[i];
				}
				return sum;
			}
		};
		int user1Index = 0;
		double[] user1Values = {1,2,3,4,5};
		X.setUserValues(user1Index, user1Values);
		int user2Index = 3;
		double[] user2Values = {1,1,1,1,1};
		X.setUserValues(user2Index, user2Values);
		double expectedSimilarity = 20;
		assertTrue(X.getUsersSimilarity(user1Index, user2Index, sim) == expectedSimilarity);
	}

}
