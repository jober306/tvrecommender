package util.collections;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import scala.Tuple3;

/**
 * A class that generates the cartesian product between three lists.
 * This class was created because the method of the same name in ListUtilities contains flatMap
 * which is a terminal operation breaking the lazy behavior. This generator is truly lazy for all elements.
 * @author Jonathan Bergeron
 *
 * @param <A> The type of first list
 * @param <B> The type of the second list
 * @param <C> The type of the third list
 */
public class CartesianProduct3<A, B, C> implements Supplier<Tuple3<A, B, C>>{
	
	final List<A> listA;
	int indexA;
	
	final List<B> listB;
	int indexB;
	
	final List<C> listC;
	int indexC;
	
	/**
	 * Constructor of this generator
	 * @param listA The first list
	 * @param listB The second list
	 * @param listC the third list
	 */
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
	
	/**
	 * Method that calculate the cartesian prodcut size.
	 * @return The cartesian product size.
	 */
	public int cartesianProductSize(){
		return listA.size() * listB.size() * listC.size();
	}
	
	/**
	 * Method that returns the stream representing the cartesian product of the three lists. 
	 * @return The stream containing tuple3 of elements of all lists.
	 */
	public Stream<Tuple3<A, B, C>> generate(){
		return Stream.generate(this).limit(cartesianProductSize());
	}

}
