package util.collections;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import scala.Tuple3;

public class CartesianProduct3<A, B, C> implements Supplier<Tuple3<A, B, C>>{
	
	final List<A> listA;
	int indexA;
	
	final List<B> listB;
	int indexB;
	
	final List<C> listC;
	int indexC;
	
	public CartesianProduct3(List<A> listA, List<B> listB, List<C> listC){
		this.listA = listA;
		this.listB = listB;
		this.listC = listC;
		indexA = 0;
		indexB = 0;
		indexC = 0;
	}

	@Override
	public Tuple3<A, B, C> get() {
		Tuple3<A, B, C> tuple = new Tuple3<>(listA.get(indexA), listB.get(indexB), listC.get(indexC));
		if(++indexC == listC.size()){
			indexC = 0;
			if(++indexB == listB.size()){
				indexB = 0;
				indexA++;
			}
		}
		return tuple;
	}
	
	public int cartesianProductSize(){
		return listA.size() * listB.size() * listC.size();
	}
	
	public Stream<Tuple3<A, B, C>> generate(){
		return Stream.generate(this).limit(cartesianProductSize());
	}

}
