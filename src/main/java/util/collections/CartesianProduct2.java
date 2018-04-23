package util.collections;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import scala.Tuple2;

/**
 * A class that generates the cartesian product between two lists.
 * This class was created because the method of the same name in ListUtilities contains flatMap
 * which is a terminal operation breaking the lazy behavior. This generator is truly lazy for all elements.
 * @author Jonathan Bergeron
 *
 * @param <A> The type of first list
 * @param <B> The type of the second list
 */
public class CartesianProduct2<A, B> implements Supplier<Tuple2<A, B>>{
	
	final List<A> listA;
	int indexA;
	
	final List<B> listB;
	int indexB;
	
	/**
	 * Constructor of this generator
	 * @param listA The first list
	 * @param listB The second list
	 */
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
	
	/**
	 * Method that calculate the cartesian prodcut size.
	 * @return The cartesian product size.
	 */
	public int cartesianProductSize(){
		return listA.size() * listB.size();
	}
	
	/**
	 * Method that returns the stream representing the cartesian product of the two lists. 
	 * @return The stream containing tuple2 of elements of all lists.
	 */
	public Stream<Tuple2<A, B>> generate(){
		return Stream.generate(this).limit(cartesianProductSize());
	}
}
