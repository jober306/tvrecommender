package algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

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
			Pair<Integer, Double> positionValue = QuickSelect.select(
					unsortedNumbers, i + 1);
			int pos = positionValue.getFirst();
			double value = positionValue.getSecond();
			assertTrue(sortedNumbers[i].equals(value));
			assertTrue(pos == (Arrays.asList(unsortedNumbers).indexOf(value)));
		}
	}

	@Test
	public void selectTopN() {
		List<Pair<Integer, Double>> top3 = QuickSelect.selectTopN(
				unsortedNumbers, 3);
		assertTrue(top3.size() == 3);
		for (int i = 0; i < top3.size(); i++) {
			Pair<Integer, Double> positionValue = top3.get(i);
			int pos = positionValue.getFirst();
			double value = positionValue.getSecond();
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
			Pair<Integer, Double> positionValue = QuickSelect.select(
					indiceValues, values, i + 1);
			int pos = positionValue.getFirst();
			double value = positionValue.getSecond();
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
		List<Pair<Integer, Double>> topN = QuickSelect.selectTopN(indiceValues,
				values, n);
		for (int i = 0; i < n; i++) {
			Pair<Integer, Double> positionValue = topN.get(i);
			int pos = positionValue.getFirst();
			double value = positionValue.getSecond();
			assertEquals(expectedIndiceValues[i], pos);
			assertEquals(expectedValues[i], value, 0.0d);
		}
	}
}
