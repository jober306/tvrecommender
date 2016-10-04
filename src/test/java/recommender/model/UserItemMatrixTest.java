package recommender.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import recommender.model.linalg.SparseVector;
import recommender.model.linalg.SparseVector.SparseVectorEntry;
import recommender.similarities.OneSimilarity;
import recommender.similarities.Similarity;
import recommender.similarities.SumSimilarity;

public class UserItemMatrixTest {

	private static final int NUMBER_OF_USERS = 4;
	private static final int NUMBER_OF_ITEMS = 5;
	private static final double[][] DATAS = { { 0, 1, 2, 3, 4 },
			{ 5, 6, 7, 8, 9 }, { 10, 11, 12, 13, 14 }, { 15, 16, 17, 18, 19 } };
	UserItemMatrix X;

	@Before
	public void setUp() {
		X = new UserItemMatrix(DATAS);
	}

	@Test
	public void testGetRating() {
		for (int row = 0; row < DATAS.length; row++) {
			for (int col = 0; col < DATAS[0].length; col++) {
				assertTrue(DATAS[row][col] == X.getRating(row, col));
			}
		}
	}

	@Test
	public void testEntriesInitializedToZero() {
		UserItemMatrix X2 = new UserItemMatrix(NUMBER_OF_USERS, NUMBER_OF_ITEMS);
		for (int user = 0; user < NUMBER_OF_USERS; user++) {
			for (int item = 0; item < NUMBER_OF_ITEMS; item++) {
				assertTrue(0 == X2.getRating(user, item));
			}
		}
	}

	@Test
	public void testGetNumberOfUsers() {
		assertEquals(NUMBER_OF_USERS, X.getNumberOfUsers());
	}

	@Test
	public void testGetNumberOfItems() {
		assertEquals(NUMBER_OF_ITEMS, X.getNumberOfItems());
	}

	@Test
	public void getUserValues() {
		for (int user = 0; user < NUMBER_OF_USERS; user++) {
			assertArrayEquals(DATAS[user], X.getUserValues(user), 0.0d);
		}
	}

	@Test
	public void getItemValues() {
		int itemIndex = 2;
		double[] expectedItemValues = { 2, 7, 12, 17 };
		assertArrayEquals(expectedItemValues, X.getItemValues(itemIndex), 0.0d);
	}

	@Test
	public void getDataMajorColumnTest() {
		double[] expectedValues = { 0, 5, 10, 15, 1, 6, 11, 16, 2, 7, 12, 17,
				3, 8, 13, 18, 4, 9, 14, 19 };
		assertArrayEquals(expectedValues, X.getDataMajorColumn(), 0.0d);
	}

	@Test
	public void getDataMajorRowTest() {
		double[] expectedValues = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16, 17, 18, 19 };
		assertArrayEquals(expectedValues, X.getDataMajorRow(), 0.0d);
	}

	@Test
	public void getUsersAsVectorTest() {
		List<IndexedRow> rows = X.getUsersAsVector();
		for (int i = 0; i < NUMBER_OF_USERS; i++) {
			int userIndex = (int) rows.get(i).index();
			assertArrayEquals(DATAS[userIndex], rows.get(i).vector().toArray(),
					0.0d);
		}
	}

	@Test
	public void getItemsAsVectorTest() {
		List<IndexedRow> cols = X.getItemsAsVector();
		for (int i = 0; i < NUMBER_OF_ITEMS; i++) {
			int itemIndex = (int) cols.get(i).index();
			assertArrayEquals(X.getItemValues(itemIndex), cols.get(i).vector()
					.toArray(), 0.0);
		}
	}

	@Test
	public void testSetMethods() {
		double expectedValue = 2.5d;
		int testedUser = 1;
		int testedItem = 2;
		X.setUserItemValue(testedUser, testedItem, expectedValue);
		assertTrue(expectedValue == X.getRating(testedUser, testedItem));

		X.setUserSeenItem(testedUser, testedItem);
		assertTrue(UserItemMatrix.USER_SEEN_ITEM_VALUE == X.getRating(
				testedUser, testedItem));

		X.setUserNotSeenItem(testedUser, testedItem);
		assertTrue(UserItemMatrix.USER_NOT_SEEN_ITEM_VALUE == X.getRating(
				testedUser, testedItem));

		double[] userValues = { 1, 2, 3, 4, 5 };
		X.setUserValues(testedUser, userValues);
		for (int i = 0; i < NUMBER_OF_ITEMS; i++) {
			assertTrue(X.getRating(testedUser, i) == userValues[i]);
		}

		double[] itemValues = { 1, 2, 3, 4 };
		X.setItemValues(testedItem, itemValues);
		for (int i = 0; i < NUMBER_OF_USERS; i++) {
			assertTrue(X.getRating(i, testedItem) == itemValues[i]);
		}
	}

	@Test
	public void testGetUsersSimilarity() {
		Similarity sim = SumSimilarity.getInstance();
		int user1Index = 0;
		double[] user1Values = { 1, 2, 3, 4, 5 };
		X.setUserValues(user1Index, user1Values);
		int user2Index = 3;
		double[] user2Values = { 1, 1, 1, 1, 1 };
		X.setUserValues(user2Index, user2Values);
		double expectedSimilarity = 20;
		assertTrue(X.getUsersSimilarity(user1Index, user2Index, sim) == expectedSimilarity);
	}

	@Test
	public void testGetItemSimilarity() {
		Similarity sim = SumSimilarity.getInstance();
		int itemIndex1 = 1;
		int itemIndex2 = 3;
		Double expectedSimilarity = 76.0d;
		assertEquals(
				new Double(X.getItemsSimilarity(itemIndex1, itemIndex2, sim)),
				expectedSimilarity);
	}

	@Test
	public void getItemIndexesSeenByUsersTest() {
		double[][] data = { { 1, 0, 4 }, { 0, 0, 2 }, { 1, 3, 4 }, { 0, 0, 0 } };
		List<Integer> expectedIndexForUser0 = Arrays.asList(0, 2);
		List<Integer> expectedIndexForUser1 = Arrays.asList(2);
		List<Integer> expectedIndexForUser2 = Arrays.asList(0, 1, 2);
		List<Integer> expectedIndexForUser3 = new ArrayList<Integer>();
		UserItemMatrix U = new UserItemMatrix(data);
		HashMap<Integer, List<Integer>> omega = U.getItemIndexesSeenByUsers();
		assertEquals(expectedIndexForUser0, omega.get(0));
		assertEquals(expectedIndexForUser1, omega.get(1));
		assertEquals(expectedIndexForUser2, omega.get(2));
		assertEquals(expectedIndexForUser3, omega.get(3));
	}

	@Test
	public void getSparseVectorRepresentationTest() {
		double[][] data = { { 1, 0, 4 }, { 0, 0, 2 }, { 1, 3, 4 }, { 0, 0, 0 } };
		UserItemMatrix U = new UserItemMatrix(data);
		SparseVector[] sparseRep = U.getUsersInSparseVectorRepresentation();
		int[][] expectedIndexVector = { { 0, 2 }, { 2 }, { 0, 1, 2 }, {} };
		double[][] expectedValueVector = { { 1, 4 }, { 2 }, { 1, 3, 4 }, {} };
		for (int i = 0; i < sparseRep.length; i++) {
			int index = 0;
			for (SparseVectorEntry entry : sparseRep[i]) {
				if (entry == null)
					break;
				assertTrue(entry.index == expectedIndexVector[i][index]);
				assertTrue(entry.value == expectedValueVector[i][index]);
				index++;
			}
		}
	}

	@Test
	public void getetUserSimilaritiesMatrixTest() {
		Similarity sim = OneSimilarity.getInstance();
		UserSimilaritiesMatrix U = X.getUserSimilaritiesMatrix(sim);
		assertEquals(NUMBER_OF_USERS, U.getNumberOfRow());
		assertEquals(NUMBER_OF_USERS, U.getNumberOfCol());
	}

	@Test
	public void testGetItemSimilaritiesMatrixTest() {
		Similarity sim = OneSimilarity.getInstance();
		ItemSimilaritiesMatrix U = X.getItemSimilaritiesMatrix(sim);
		assertEquals(NUMBER_OF_ITEMS, U.getNumberOfRow());
		assertEquals(NUMBER_OF_ITEMS, U.getNumberOfCol());
	}

	@After
	public void tearDown() {
		X = null;
	}
}
