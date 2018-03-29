package util;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.*;
import java.util.stream.StreamSupport;

import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

public class StreamUtilities {
	
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
	
	public static<A, B> Stream<Tuple2<A, B>> zip(Stream<? extends A> a, Stream<? extends B> b){
		return zip(a, b, (c, d) -> new Tuple2<>(c, d));
	}
	
	public static<A,B,C> Stream<Tuple3<A,B,C>> zip(Stream<? extends A> a, Stream<? extends B> b, Stream<? extends C> c){
		Stream<Tuple2<A, B>> ab = zip(a, b);
		return zip(ab, c, (d, e) -> new Tuple3<A, B, C>(d._1(), d._2(), e));
	}
	
	public static<A, B, C, D> Stream<Tuple4<A, B, C, D>> zip(Stream<? extends A> a, Stream<? extends B> b, Stream<? extends C> c, Stream<? extends D> d){
		Stream<Tuple3<A, B, C>> abc = zip(a, b, c);
		return zip(abc, d, (e, f) -> new Tuple4<A, B, C, D>(e._1(), e._2(), e._3(), f));
	}
}
