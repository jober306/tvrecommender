package util.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scala.Tuple3;
import util.collections.StreamUtilities;

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
	
	@After
	public void tearDown(){
		this.streamA = null;
		this.streamB = null;
		this.streamC = null;
	}
}
