package util.collections;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

/**
 * Class that offers utilities methods related to java8 stream.
 * @author Jonathan Bergeron
 *
 */
public class StreamUtilities {
	
	/**
	 * Method that zip two stream with the given zipper.
	 * @param a The first stream
	 * @param b The second stream
	 * @param zipper The function to zip elements of a and b together.
	 * @return The stream containing zipped elements of a and b. Keeps the order.
	 */
	public static<A, B, C> Stream<C> zip(Stream<? extends A> a, Stream<? extends B> b, BiFunction<? super A, ? super B, ? extends C> zipper) {
		Objects.requireNonNull(zipper);
		Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
		Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();
		
		// Zipping looses DISTINCT and SORTED characteristics
		int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics() &
		~(Spliterator.DISTINCT | Spliterator.SORTED);
		
		long zipSize = ((characteristics & Spliterator.SIZED) != 0)
		? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
		: -1;
		
		Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
		Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
		Iterator<C> cIterator = new Iterator<C>() {
			@Override
			public boolean hasNext() {
			return aIterator.hasNext() && bIterator.hasNext();
			}
			
			@Override
			public C next() {
			return zipper.apply(aIterator.next(), bIterator.next());
			}
		};
		
		Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
		return (a.isParallel() || b.isParallel())
		? StreamSupport.stream(split, true)
		: StreamSupport.stream(split, false);
	}
	
	/**
	 * Method that zip a stream with its corresponding index. If the stream is not ordered the index will
	 * not correspond to the original source.
	 * @param a The stream to zip with index
	 * @return A stream of tuple2 containing the source elements with its corresponding index starting at 0.
	 */
	public static <A> Stream<Tuple2<A, Integer>> zipWithIndex(Stream<? extends A> a){
		Stream<Integer> indexStream = Stream.generate(new IndexesSupplier());
		return zip(a, indexStream);
	}
	
	/**
	 * Method that zip two streams together into stream of tuple2.
	 * @param a The first stream
	 * @param b The second stream
	 * @return A stream of tuple2 containing elements of a and b.
	 */
	public static<A, B> Stream<Tuple2<A, B>> zip(Stream<? extends A> a, Stream<? extends B> b){
		return zip(a, b, (c, d) -> new Tuple2<>(c, d));
	}
	
	/**
	 * Method that zip three streams together into stream of tuple3.
	 * @param a The first stream
	 * @param b The second stream
	 * @param c The third stream
	 * @return A stream of tuple3 containing elements of a, b and c.
	 */
	public static<A,B,C> Stream<Tuple3<A,B,C>> zip(Stream<? extends A> a, Stream<? extends B> b, Stream<? extends C> c){
		Stream<Tuple2<A, B>> ab = zip(a, b);
		return zip(ab, c, (d, e) -> new Tuple3<A, B, C>(d._1(), d._2(), e));
	}
	
	/**
	 * Method that zip four streams together into stream of tuple4.
	 * @param a The first stream
	 * @param b The second stream
	 * @param c The third stream
	 * @param d The fourth stream
	 * @return A stream of tuple4 containing elements of a, b, c and d.
	 */
	public static<A, B, C, D> Stream<Tuple4<A, B, C, D>> zip(Stream<? extends A> a, Stream<? extends B> b, Stream<? extends C> c, Stream<? extends D> d){
		Stream<Tuple3<A, B, C>> abc = zip(a, b, c);
		return zip(abc, d, (e, f) -> new Tuple4<A, B, C, D>(e._1(), e._2(), e._3(), f));
	}
	
	/**
	 * Method that creates a map given a key mapper and a score mapper. It calculates the average score
	 * given multiple element keys of the stream collides while creating map.
	 * @param stream The original stream
	 * @param keyMapper A function that maps element from the stream to the map key.
	 * @param scoreMapper A function that maps element from the stream to a double score.
	 * @param <T> The type of the stream. 
	 * @param <K> The type of the mapped key.
	 * @return A map of T and double containing the average score for each key.
	 */
	public static <T, K> Map<K, Double> toMapAverage(Stream<T> stream, Function<? super T,? extends K> keyMapper, Function<? super T,Double> scoreMapper){
		return stream.collect(Collectors.toMap(keyMapper, t -> new Tuple2<Double, Integer>(scoreMapper.apply(t), 1), (scoreCount1, scoreCount2) -> new Tuple2<>(scoreCount1._1() + scoreCount2._1(), scoreCount1._2() + scoreCount2._2())))
		.entrySet().stream().collect(Collectors.toMap(Entry::getKey, k -> k.getValue()._1() / k.getValue()._2()));
	}
}
