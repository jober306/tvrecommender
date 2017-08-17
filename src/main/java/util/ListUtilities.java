package util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;

import scala.Tuple2;

/**
 * Class that encapsulates utility methods on collections using the java 8
 * lambda expressions.
 * 
 * @author Jonathan Bergeron
 *
 */
public class ListUtilities {

	/**
	 * Method that return a list with only the first argument of a list of
	 * tuples.
	 * 
	 * @param tupleList
	 *            The list of tuples.
	 * @return A list containing only the first element of the tuple.
	 */
	public static <E, U> List<E> getFirstArgumentAsList(List<Tuple2<E, U>> tupleList) {
		return tupleList.stream().map(Tuple2::_1).collect(Collectors.toList());
	}

	/**
	 * Method that return a list with only the second argument of a list of
	 * tuples.
	 * 
	 * @param tupleList
	 *            The list of tuples.
	 * @return A list containing only the second element of the tuple.
	 */
	public static <E, U> List<U> getSecondArgumentAsList(List<Tuple2<E, U>> tupleList) {
		return tupleList.stream().map(Tuple2::_2).collect(Collectors.toList());
	}
	
	/**
	 * Method that creates an array containing all the first argument of a list of tuple.
	 * @param c The class of the first element in the tuple
	 * @param tupleList The list of tuple elements
	 * @return An array containing all the first argument of the list of tuples. The ordering is kept.
	 */
	public static <E, U> E[] getFirstArgumentAsArray(Class<E> c, List<Tuple2<E,U>> tupleList){
		return convertListToArray(c, getFirstArgumentAsList(tupleList));
	}
	
	/**
	 * Method that creates an array containing all the second argument of a list of tuple.
	 * @param c The class of the second element in the tuple
	 * @param tupleList The list of tuple elements
	 * @return An array containing all the second argument of the list of tuples. The ordering is kept.
	 */
	public static <E, U> U[] getSecondArgumentAsArray(Class<U> c, List<Tuple2<E, U>> tupleList){
		return convertListToArray(c, getSecondArgumentAsList(tupleList));
	}
	
	private static <E> E[] convertListToArray(Class<E> c, List<E> list){
		@SuppressWarnings("unchecked")
		E[] array = (E[])Array.newInstance(c, list.size());
		return list.toArray(array);
	}

	/**
	 * Method that does the intersection of two lists. The method is using the
	 * contain method to check equality which in return uses the equals method.
	 * 
	 * @param l1
	 *            The first list.
	 * @param l2
	 *            The second list.
	 * @return The intersection of l1 and l2.
	 */
	public static <U> List<U> intersection(List<U> l1, List<U> l2) {
		return l1.stream().filter(l2::contains).collect(Collectors.toList());
	}

	/**
	 * Method that subtract a list to another.
	 * 
	 * @param original
	 *            The list who will get elements deleted.
	 * @param listToSubstract
	 *            The list containing the elements to delete.
	 * @return The list containing elements from original minus the elements of
	 *         listToSubstract.
	 */
	public static <U> List<U> substract(List<U> original,
			List<U> listToSubstract) {
		return original.stream()
				.filter(element -> !listToSubstract.contains(element))
				.collect(Collectors.toList());
	}

	/**
	 * Method that adds all the elements of l2 to l1.
	 * 
	 * @param l1
	 *            The first collection.
	 * @param l2
	 *            The second collection.
	 * @return The first collection with elements of l2 added.
	 */
	public static <U> List<U> union(List<U> l1, List<U> l2) {
		l1.addAll(l2);
		return l1;
	}
}
