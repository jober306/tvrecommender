package util;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import scala.Tuple2;

public class CartesianProduct2<A, B> implements Supplier<Tuple2<A, B>>{
	
	final List<A> listA;
	int indexA;
	
	final List<B> listB;
	int indexB;
	
	public CartesianProduct2(List<A> listA, List<B> listB){
		this.listA = listA;
		this.listB = listB;
		indexA = 0;
		indexB = 0;
	}

	@Override
	public Tuple2<A, B> get() {
		Tuple2<A, B> tuple = new Tuple2<>(listA.get(indexA), listB.get(indexB));
		if(++indexB == listB.size()){
			indexB = 0;
			indexA++;
		}
		return tuple;
	}
	
	public int cartesianProductSize(){
		return listA.size() * listB.size();
	}
	
	public Stream<Tuple2<A, B>> generate(){
		return Stream.generate(this).limit(cartesianProductSize());
	}
}
