package util.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import scala.Tuple2;
import scala.Tuple3;

public class StreamUtilitiesTest {

	static final List<String> EXPECTED_ZIP_CONCAT = Arrays.asList("OhHey", "HeyOh", "HaaH");
	static final List<Tuple3<String, String, Integer>> EXPECTED_ZIP3 = Arrays.asList(new Tuple3<>("Oh", "Hey", 1), new Tuple3<>("Hey", "Oh", 2), new Tuple3<>("Ha", "aH", 3));
	
	Stream<String> streamA;
	Stream<String> streamB;
	Stream<Integer> streamC;
	
	@Before
	public void setUp(){
		this.streamA = Stream.of("Oh", "Hey", "Ha");
		this.streamB = Stream.of("Hey", "Oh", "aH");
		this.streamC = Stream.of(1,2,3);
	}
	
	@Test
	public void zipTwoStreamsSameSizeTest(){
		List<String> actual_zip_concat = StreamUtilities.zip(streamA, streamB, (a, b) -> a + b).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP_CONCAT, actual_zip_concat);
	}
	
	@Test
	public void zipTwoStreamsFirstOneBiggerTest(){
		this.streamA = Stream.concat(streamA, Stream.of("Homer"));
		List<String> actual_zip_concat = StreamUtilities.zip(streamA, streamB, (a, b) -> a + b).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP_CONCAT, actual_zip_concat);
	}
	
	@Test
	public void zipTwoStreamsSecondOneBiggerTest(){
		this.streamB = Stream.concat(streamB, Stream.of("Homer"));
		List<String> actualZipConcat = StreamUtilities.zip(streamA, streamB, (a, b) -> a + b).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP_CONCAT, actualZipConcat);
	}
	
	@Test
	public void zipThreeStreamsAllSameSizeTest(){
		List<Tuple3<String, String, Integer>> zippedStream = StreamUtilities.zip(streamA, streamB, streamC).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP3, zippedStream);
	}
	
	@Test
	public void zipThreeStreamsThirdOneBiggerTest(){
		this.streamC = Stream.concat(streamC, Stream.of(4));
		List<Tuple3<String, String, Integer>> zippedStream = StreamUtilities.zip(streamA, streamB, streamC).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP3, zippedStream);
	}
	
	@Test
	public void zipThreeStreamsThirdOneSmallerTest(){
		this.streamC = streamC.limit(2);
		List<Tuple3<String, String, Integer>> zippedStream = StreamUtilities.zip(streamA, streamB, streamC).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP3.subList(0, 2), zippedStream);
	}
	
	@Test
	public void zipThreeStreamsFirstOneBiggerTest(){
		this.streamA = Stream.concat(streamA, Stream.of("Hi"));
		List<Tuple3<String, String, Integer>> zippedStream = StreamUtilities.zip(streamA, streamB, streamC).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP3, zippedStream);
	}
	
	@Test
	public void zipThreeStreamsFirstOneSmallerTest(){
		this.streamA = streamA.limit(2);
		List<Tuple3<String, String, Integer>> zippedStream = StreamUtilities.zip(streamA, streamB, streamC).collect(Collectors.toList());
		assertEquals(EXPECTED_ZIP3.subList(0, 2), zippedStream);
	}
	
	@Test
	public void testZipWithOneEmptyStream(){
		List<String> actualStream = StreamUtilities.zip(streamA, Stream.of(), (a, b) -> a + b).collect(Collectors.toList());
		assertTrue(actualStream.isEmpty());
	}
	
	@Test
	public void testZipWithIndexEmptyStream() {
		Stream<Integer> emptyStream = Stream.empty();
		List<Tuple2<Integer, Integer>> indexedEmptyStream = StreamUtilities.zipWithIndex(emptyStream).collect(Collectors.toList());
		assertTrue(indexedEmptyStream.isEmpty());
	}
	
	@Test
	public void testZipWithIndexNormalStream() {
		List<Tuple2<Integer, Integer>> expectedStream = Arrays.asList(new Tuple2<>(1,0), new Tuple2<>(2,1), new Tuple2<>(3,2));
		List<Tuple2<Integer, Integer>> actualStream = StreamUtilities.zipWithIndex(streamC).collect(Collectors.toList());
		assertEquals(expectedStream, actualStream);
	}
	
	@Test
	public void testToMapAverageEmptyStream() {
		Stream<Double> emptyStream = Stream.empty();
		Map<Double, Double> emptyStreamMapAverage = StreamUtilities.toMapAverage(emptyStream, v -> v, v -> v);
		assertTrue(emptyStreamMapAverage.isEmpty());
	}
	
	@Test
	public void testToMapAverageNoDuplicateStream() {
		Map<Integer, Double> actualMapAverage = StreamUtilities.toMapAverage(streamC, v -> v, Double::new);
		Map<Integer, Double> expectedMapAverage = Maps.newHashMap();
		expectedMapAverage.put(1, 1.0d);
		expectedMapAverage.put(2, 2.0d);
		expectedMapAverage.put(3, 3.0d);
		assertEquals(expectedMapAverage, actualMapAverage);
	}
	
	@Test
	public void testToMapAverageWithDuplicateStream() {
		Stream<Tuple2<Integer, Double>> keyScoreStream = Stream.of(new Tuple2<>(1, 1.0d), new Tuple2<>(1, 2.0d), new Tuple2<>(2, 2.0d), new Tuple2<>(2,5.0d), new Tuple2<>(3, 1.0d));
		Map<Integer, Double> actualMapAverage = StreamUtilities.toMapAverage(keyScoreStream, Tuple2::_1, Tuple2::_2);
		Map<Integer, Double> expectedMapAverage = Maps.newHashMap();
		expectedMapAverage.put(1, 1.5d);
		expectedMapAverage.put(2, 3.5d);
		expectedMapAverage.put(3, 1.0d);
		assertEquals(expectedMapAverage, actualMapAverage);
	}
	
	@After
	public void tearDown(){
		this.streamA = null;
		this.streamB = null;
		this.streamC = null;
	}
}
