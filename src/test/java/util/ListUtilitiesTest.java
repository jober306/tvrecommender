package util;

import static org.junit.Assert.assertTrue;
import static util.ListUtilities.getFirstArgument;
import static util.ListUtilities.getSecondArgument;
import static util.ListUtilities.intersection;
import static util.ListUtilities.substract;
import static util.ListUtilities.union;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import scala.Tuple2;

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
		List<Integer> firstArgs = (List<Integer>) getFirstArgument(tupleList);
		for (int i = 0; i < 10; i++) {
			assertTrue(firstArgs.contains(i));
		}
	}

	@Test
	public void getSecondArgumentTest() {
		List<String> secondArgs = (List<String>) getSecondArgument(tupleList);
		for (int i = 0; i < 10; i++) {
			assertTrue(secondArgs.contains(Integer.toString(i)));
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
}
