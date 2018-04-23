package util.collections;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static util.collections.ListUtilities.getFirstArgumentAsArray;
import static util.collections.ListUtilities.getFirstArgumentAsList;
import static util.collections.ListUtilities.getSecondArgumentAsArray;
import static util.collections.ListUtilities.getSecondArgumentAsList;
import static util.collections.ListUtilities.intersection;
import static util.collections.ListUtilities.substract;
import static util.collections.ListUtilities.union;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import scala.Tuple2;
import scala.Tuple3;

public class ListUtilitiesTest {

	static List<Tuple2<Integer, String>> tupleList;
	static List<Integer> l1;
	static List<Integer> l2;

	@BeforeClass
	public static void SetupOnce() {
		tupleList = new ArrayList<Tuple2<Integer, String>>();
		for (int i = 0; i < 10; i++) {
			tupleList.add(new Tuple2<Integer, String>(i, Integer.toString(i)));
		}
		l1 = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			l1.add(i);
		}
		l2 = new ArrayList<Integer>();
		for (int i = 4; i < 11; i++) {
			l2.add(i);
		}
	}

	@Test
	public void getFirstArgumentTest() {
		List<Integer> firstArgs = (List<Integer>) getFirstArgumentAsList(tupleList);
		for (int i = 0; i < 10; i++) {
			assertTrue(firstArgs.contains(i));
		}
	}
	
	@Test
	public void getFirstArgumentAsArrayTest() {
		Integer[] firstArgs = getFirstArgumentAsArray(Integer.class, tupleList);
		for(int i = 0; i < 10; i++){
			assertTrue(firstArgs[i] == i);
		}
	}

	@Test
	public void getSecondArgumentTest() {
		List<String> secondArgs = (List<String>) getSecondArgumentAsList(tupleList);
		for (int i = 0; i < 10; i++) {
			assertTrue(secondArgs.contains(Integer.toString(i)));
		}
	}
	
	@Test
	public void getSecondArgumentAsArrayTest() {
		String[] secondArgs = getSecondArgumentAsArray(String.class, tupleList);
		for(int i = 0; i < 10; i++){
			assertTrue(secondArgs[i].equals(Integer.toString(i)));
		}
	}
	
	@Test
	public void intersectionTest() {
		List<Integer> intersection = intersection(l1, l2);
		for (int i = 4; i < 7; i++) {
			assertTrue(intersection.contains(i));
		}
	}

	@Test
	public void substractTest() {
		List<Integer> substractedList = substract(l1, l2);
		for (int i = 0; i < 3; i++) {
			assertTrue(substractedList.contains(i));
		}
	}

	@Test
	public void unionTest() {
		List<Integer> oldl1 = new ArrayList<Integer>(l1);
		List<Integer> union = union(l1, l2);
		for (int i = 0; i < 11; i++) {
			assertTrue(union.contains(i));
		}
		l1 = oldl1;
	}
	
	@Test
	public void cartesianProductTwoListsOneEmptyTest(){
		List<String> emptyList = Collections.emptyList();
		List<String> list = Arrays.asList("hoho", "haha");
		List<Tuple2<String, String>> cartesianProductLeft = ListUtilities.cartesianProduct(emptyList, list);
		List<Tuple2<String, String>> cartesianProductRight = ListUtilities.cartesianProduct(list, emptyList);
		assertTrue(cartesianProductLeft.isEmpty());
		assertTrue(cartesianProductRight.isEmpty());
	}
	
	@Test
	public void cartesianProductTwoEmptyLists(){
		List<String> emptyList1 = Collections.emptyList();
		List<Integer> emptyList2 = Collections.emptyList();
		List<Tuple2<String, Integer>> cartesianProduct = ListUtilities.cartesianProduct(emptyList1, emptyList2);
		assertTrue(cartesianProduct.isEmpty());
	}
	
	@Test
	public void cartesianProduct2Lists(){
		List<String> list1 = Arrays.asList("hoho", "haha");
		List<Integer> list2 = Arrays.asList(1, 2);
		List<Tuple2<String, Integer>> cartesianProduct = ListUtilities.cartesianProduct(list1, list2);
		int expectedSize = list1.size() * list2.size();
		List<Tuple2<String, Integer>> expectedResult = Arrays.asList(new Tuple2<String, Integer>("hoho", 1),new Tuple2<String, Integer>("hoho", 2),new Tuple2<String, Integer>("haha", 1),new Tuple2<String, Integer>("haha", 2));
		assertEquals(expectedSize, cartesianProduct.size());
		assertEquals(expectedResult, cartesianProduct);
	}
	
	@Test
	public void cartesianProduct2Supplier(){
		List<String> list1 = Arrays.asList("hoho", "haha");
		List<Integer> list2 = Arrays.asList(1, 2);
		List<Tuple2<String, Integer>> cartesianProduct = new CartesianProduct2<String, Integer>(list1, list2).generate().collect(toList());
		int expectedSize = list1.size() * list2.size();
		List<Tuple2<String, Integer>> expectedResult = Arrays.asList(new Tuple2<String, Integer>("hoho", 1),new Tuple2<String, Integer>("hoho", 2),new Tuple2<String, Integer>("haha", 1),new Tuple2<String, Integer>("haha", 2));
		assertEquals(expectedSize, cartesianProduct.size());
		assertEquals(expectedResult, cartesianProduct);
	}
	
	@Test
	public void cartesianProduct3Supplier(){
		List<String> list1 = Arrays.asList("hoho", "haha");
		List<Integer> list2 = Arrays.asList(1, 2);
		List<Double> list3 = Arrays.asList(5.0d);
		List<Tuple3<String, Integer, Double>> cartesianProduct = new CartesianProduct3<String, Integer, Double>(list1, list2, list3).generate().collect(toList());
		int expectedSize = list1.size() * list2.size() * list3.size();
		List<Tuple3<String, Integer, Double>> expectedResult = Arrays.asList(new Tuple3<String, Integer, Double>("hoho", 1, 5.0d),new Tuple3<String, Integer, Double>("hoho", 2, 5.0d),new Tuple3<String, Integer, Double>("haha", 1, 5.0d),new Tuple3<String, Integer, Double>("haha", 2, 5.0d));
		assertEquals(expectedSize, cartesianProduct.size());
		assertEquals(expectedResult, cartesianProduct);
	}
	
	@Test
	public void cartesianProduct3Lists(){
		List<String> list1 = Arrays.asList("hoho", "haha");
		List<Integer> list2 = Arrays.asList(1, 2);
		List<Double> list3 = Arrays.asList(5.0d);
		List<Tuple3<String, Integer, Double>> cartesianProduct = ListUtilities.cartesianProduct(list1, list2, list3);
		int expectedSize = list1.size() * list2.size() * list3.size();
		List<Tuple3<String, Integer, Double>> expectedResult = Arrays.asList(new Tuple3<String, Integer, Double>("hoho", 1, 5.0d),new Tuple3<String, Integer, Double>("hoho", 2, 5.0d),new Tuple3<String, Integer, Double>("haha", 1, 5.0d),new Tuple3<String, Integer, Double>("haha", 2, 5.0d));
		assertEquals(expectedSize, cartesianProduct.size());
		assertEquals(expectedResult, cartesianProduct);
	}
	
	@Test
	public void cartesianProduct3Streams(){
		List<String> list1 = Arrays.asList("hoho", "haha");
		List<Integer> list2 = Arrays.asList(1, 2);
		List<Double> list3 = Arrays.asList(5.0d);
		List<Tuple3<String, Integer, Double>> cartesianProduct = ListUtilities.cartesianProductStream(list1, list2, list3).collect(toList());
		int expectedSize = list1.size() * list2.size() * list3.size();
		List<Tuple3<String, Integer, Double>> expectedResult = Arrays.asList(new Tuple3<String, Integer, Double>("hoho", 1, 5.0d),new Tuple3<String, Integer, Double>("hoho", 2, 5.0d),new Tuple3<String, Integer, Double>("haha", 1, 5.0d),new Tuple3<String, Integer, Double>("haha", 2, 5.0d));
		assertEquals(expectedSize, cartesianProduct.size());
		assertEquals(expectedResult, cartesianProduct);
	}
}
