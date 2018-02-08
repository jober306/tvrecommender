package util;

import static org.junit.Assert.assertEquals;
import static util.CurryingUtilities.curry1;
import static util.CurryingUtilities.curry2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CurryingUtilitiesTest {
	
	BiFunction<Integer, Integer, String> addToString;
	BiFunction<String, String, String> concatenateString;
	BiConsumer<List<Integer>, Integer> addToList;
	List<Integer> list;
	
	@Before
	public void setUp(){
		addToString = (a, b) -> Integer.toString(a + b);
		concatenateString = (a, b) -> a + b;
		addToList = (list, a) -> list.add(a);
		list = new ArrayList<Integer>();
	}
	
	@Test
	public void curry1FunctionTest(){
		Function<Integer, String> curriedAddToStringFunction = curry1(addToString, 3);
		Function<String, String> curriedConcatenateStringFunction = curry1(concatenateString, "haha");
		String expectedAnswer1 = "5";
		String expectedAnswer2 = "10"; 
		assertEquals(expectedAnswer1, curriedAddToStringFunction.apply(2));
		assertEquals(expectedAnswer2, curriedAddToStringFunction.apply(7));
		String expectedAnswer3 = "hahahoho";
		String expectedAnswer4 = "hahahihi";
		assertEquals(expectedAnswer3, curriedConcatenateStringFunction.apply("hoho"));
		assertEquals(expectedAnswer4, curriedConcatenateStringFunction.apply("hihi"));
	}
	
	@Test
	public void curry1ConsumerTest(){
		Consumer<Integer> curriedAddToListFunction = curry1(addToList, list);
		curriedAddToListFunction.accept(1);
		curriedAddToListFunction.accept(2);
		curriedAddToListFunction.accept(3);
		List<Integer> expectedResult = Arrays.asList(1,2,3);
		assertEquals(expectedResult, list);
	}
	
	@Test
	public void curry2ConsumerTest(){
		Consumer<List<Integer>> curriedAddToListFunction = curry2(addToList, 3);
		curriedAddToListFunction.accept(list);
		curriedAddToListFunction.accept(list);
		curriedAddToListFunction.accept(list);
		List<Integer> expectedResult = Arrays.asList(3,3,3);
		assertEquals(expectedResult, list);
	}
	
	@Test
	public void curry2FunctionTest(){
		Function<Integer, String> curriedAddToStringFunction = curry2(addToString, 3);
		Function<String, String> curriedConcatenateStringFunction = curry2(concatenateString, "haha");
		String expectedAnswer1 = "5";
		String expectedAnswer2 = "10"; 
		assertEquals(expectedAnswer1, curriedAddToStringFunction.apply(2));
		assertEquals(expectedAnswer2, curriedAddToStringFunction.apply(7));
		String expectedAnswer3 = "hohohaha";
		String expectedAnswer4 = "hihihaha";
		assertEquals(expectedAnswer3, curriedConcatenateStringFunction.apply("hoho"));
		assertEquals(expectedAnswer4, curriedConcatenateStringFunction.apply("hihi"));
	}
	
	@After
	public void tearDown(){
		addToString = null;
		concatenateString = null;
		addToList = null;
		list = null;
	}
}	
