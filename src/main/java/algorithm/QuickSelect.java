package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import scala.Tuple2;

public class QuickSelect {

	/***********************************************************************
	 * Author: Isai Damier Title: Find Kth Greatest Value Project: geekviewpoint
	 * Package: algorithms
	 *
	 * Statement: Given a list of values, find the highest kth. Assume indexing
	 * starts at one and not at zero so that the greatest number in the list is
	 * the 1st and not the 0th number.
	 *
	 * Time Complexity: average = O(n log n); worse O(n^2)
	 * 
	 * Sample Input: {21,3,34,5,13,8,2,55,1,19}; 4 Sample Output: 19
	 * 
	 * Details: This is a selection algorithm, where the task is to select an
	 * elite out of a group. In the sample input, for instance, we are to select
	 * the 4th greatest number in the list; which happens to be 13 since 55, 34,
	 * and 21 are all greater than 13.
	 * 
	 * Generally, selection algorithms are modified sort algorithms; where
	 * instead of sorting the whole list, we sort up to the kth value. Hence, a
	 * selection algorithm is bounded by whatever sort algorithm is used to
	 * implement it.
	 * 
	 * Here for example we are using quickselect to find the kth largest value.
	 * Consequently, this algorithm is bounded by quicksort; leading to a worse
	 * case time complexity of O(n^2) and an average case time complexity of O(
	 * n log n).
	 * 
	 * Note: Finding the kth largest is essentially the same as finding the kth
	 * smallest.
	 * 
	 **********************************************************************/
	public static Tuple2<Integer, Double> select(Double[] G, int k) {
		k = k > G.length ? G.length : k;
		List<Tuple2<Integer, Double>> Gp = new ArrayList<Tuple2<Integer, Double>>();
		for (int i = 0; i < G.length; i++) {
			Gp.add(new Tuple2<Integer, Double>(i, G[i]));
		}
		return quickselect(Gp, 0, G.length - 1, k - 1);
	}

	public static List<Tuple2<Integer, Double>> selectTopN(Double[] G, int k) {
		k = k > G.length ? G.length : k;
		List<Tuple2<Integer, Double>> topN = new ArrayList<Tuple2<Integer, Double>>();
		for (int i = 1; i <= k; i++) {
			topN.add(QuickSelect.select(G, i));
		}
		return topN;
	}

	public static List<Tuple2<Integer, Double>> selectTopN(int[] indices,
			double[] values, int k) {
		List<Tuple2<Integer, Double>> topN = new ArrayList<Tuple2<Integer, Double>>();
		for (int i = 1; i <= k; i++) {
			topN.add(QuickSelect.select(indices, values, i));
		}
		return topN;
	}

	public static Tuple2<Integer, Double> select(int indices[], double values[],
			int k) {
		List<Tuple2<Integer, Double>> Gp = new ArrayList<Tuple2<Integer, Double>>();
		for (int i = 0; i < indices.length; i++) {
			Gp.add(new Tuple2<Integer, Double>(indices[i], values[i]));
		}
		return quickselect(Gp, 0, indices.length - 1, k - 1);
	}

	private static Tuple2<Integer, Double> quickselect(
			List<Tuple2<Integer, Double>> Gp, int first, int last, int k) {
		if (first <= last) {
			int pivot = partition(Gp, first, last);
			if (pivot == k) {
				return Gp.get(k);
			}
			if (pivot > k) {
				return quickselect(Gp, first, pivot - 1, k);
			}
			return quickselect(Gp, pivot + 1, last, k);
		}
		return new Tuple2<Integer, Double>(-1, 0.0d);
	}

	private static int partition(List<Tuple2<Integer, Double>> Gp, int first,
			int last) {
		int pivot = first + new Random().nextInt(last - first + 1);
		swap(Gp, last, pivot);
		for (int i = first; i < last; i++) {
			if (Gp.get(i)._2() > Gp.get(last)._2()) {
				swap(Gp, i, first);
				first++;
			}
		}
		swap(Gp, first, last);
		return first;
	}

	private static void swap(List<Tuple2<Integer, Double>> Gp, int x, int y) {
		Collections.swap(Gp, x, y);
	}
}
