package algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scala.Tuple2;

public class QuickSelectTest {

	Double[] sortedNumbers = new Double[6];
	Double[] unsortedNumbers = new Double[6];
	double[] data = { 5, 3, 1.5, 1, 0.7, 0 };

	@Before
	public void setUp() {
		for (int i = 0; i < data.length; i++) {
			sortedNumbers[i] = data[i];
			unsortedNumbers[i] = data[i];
		}
		List<Double> shuffledNumber = Arrays.asList(unsortedNumbers);
		Collections.shuffle(shuffledNumber);
		unsortedNumbers = shuffledNumber.toArray(new Double[6]);
	}

	@Test
	public void selectTest() {
		for (int i = 0; i < sortedNumbers.length; i++) {
			Tuple2<Integer, Double> positionValue = QuickSelect.select(
					unsortedNumbers, i + 1);
			int pos = positionValue._1();
			double value = positionValue._2();
			assertTrue(sortedNumbers[i].equals(value));
			assertTrue(pos == (Arrays.asList(unsortedNumbers).indexOf(value)));
		}
	}

	@Test
	public void selectTopN() {
		List<Tuple2<Integer, Double>> top3 = QuickSelect.selectTopN(
				unsortedNumbers, 3);
		assertTrue(top3.size() == 3);
		for (int i = 0; i < top3.size(); i++) {
			Tuple2<Integer, Double> positionValue = top3.get(i);
			int pos = positionValue._1();
			double value = positionValue._2();
			assertTrue(sortedNumbers[i].equals(value));
			assertTrue(pos == (Arrays.asList(unsortedNumbers).indexOf(value)));
		}
	}

	@Test
	public void selectWithIndicesValues() {
		double[] values = new double[] { 2.0d, 1.0d, 5.0d, 3.3d };
		int[] indiceValues = new int[] { 14, 11, 20, 3 };
		double[] expectedValues = new double[] { 5.0d, 3.3d, 2.0d, 1.0d };
		int[] expectedIndiceValues = new int[] { 20, 3, 14, 11 };
		for (int i = 0; i < values.length; i++) {
			Tuple2<Integer, Double> positionValue = QuickSelect.select(
					indiceValues, values, i + 1);
			int pos = positionValue._1();
			double value = positionValue._2();
			assertEquals(expectedIndiceValues[i], pos);
			assertEquals(expectedValues[i], value, 0.0d);
		}
	}

	@Test
	public void selectTopNWithIndicesValues() {
		int n = 3;
		double[] values = new double[] { 2.0d, 1.0d, 5.0d, 3.3d };
		int[] indiceValues = new int[] { 14, 11, 20, 3 };
		double[] expectedValues = new double[] { 5.0d, 3.3d, 2.0d };
		int[] expectedIndiceValues = new int[] { 20, 3, 14 };
		List<Tuple2<Integer, Double>> topN = QuickSelect.selectTopN(indiceValues,
				values, n);
		for (int i = 0; i < n; i++) {
			Tuple2<Integer, Double> positionValue = topN.get(i);
			int pos = positionValue._1();
			double value = positionValue._2();
			assertEquals(expectedIndiceValues[i], pos);
			assertEquals(expectedValues[i], value, 0.0d);
		}
	}

	@Test
	public void selectTopNWithNBiggerThanNumberOfEntries() {
		int n = 0;
		double[] values = new double[] {};
		int[] indiceValues = new int[] {};
		double[] expectedValues = new double[] { 5.0d, 3.3d, 2.0d };
		int[] expectedIndiceValues = new int[] { 20, 3, 14 };
		List<Tuple2<Integer, Double>> topN = QuickSelect.selectTopN(indiceValues,
				values, n);
		for (int i = 0; i < n; i++) {
			Tuple2<Integer, Double> positionValue = topN.get(i);
			int pos = positionValue._1();
			double value = positionValue._2();
			assertEquals(expectedIndiceValues[i], pos);
			assertEquals(expectedValues[i], value, 0.0d);
		}
	}
}
