package util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class that offers utilities methods to curry java 8 function.
 * @author Jonathan Bergeron
 *
 */
public class CurryingUtilities {
	
	/**
	 * Curry the first argument of a function.
	 * @param biFunc The bifunction to curry.
	 * @return A function that takes as input the first argument of bifucntion and return a function that
	 * takes the second argument and return the bifunction result.
	 */
	public static <T, U, R> Function<T, Function<U, R>> curry1(BiFunction<T, U, R> biFunc){
		return arg1 -> arg2 -> biFunc.apply(arg1, arg2);
	}
	
	/**
	 * Curry the first argument of a function and apply the given first argument.
	 * @param biFunc The bifunction to curry.
	 * @param arg1 The first argument to pass to the curried function.
	 * @return A function that takes as input the second argument and return the bifunction result.
	 */
	public static <T, U, R> Function<U, R> curry1(BiFunction<T, U, R> biFunc, T arg1){
		return curry1(biFunc).apply(arg1);
	}
	
	/**
	 * Curry the first argument of a biConsumer.
	 * @param biCons The biconsumer to curry.
	 * @return A function that takes as input the first argument of biconsumer and return a consumer that
	 * takes the second argument.
	 */
	public static <T, U> Function<T, Consumer<U>> curry1(BiConsumer<T, U> biCons){
		return arg1 -> arg2 -> biCons.accept(arg1, arg2);
	}
	
	/**
	 * Curry the first argument of a biconsumer and apply the given first argument.
	 * @param biCons The biconsumer to curry.
	 * @param arg1 The first argument to pass to the curried biconsumer.
	 * @return A consumer that takes as input the second argument.
	 */
	public static <T, U> Consumer<U> curry1(BiConsumer<T, U> biCons, T arg1){
		return curry1(biCons).apply(arg1);
	}
	
	/**
	 * Curry the second argument of a function.
	 * @param biFunc The bifunction to curry.
	 * @return A function that takes as input the second argument of bifucntion and return a function that
	 * takes the first argument and return the bifunction result.
	 */
	public static <T, U, R> Function<U, Function<T, R>> curry2(BiFunction<T, U, R> biFunc){
		return arg1 -> arg2 -> biFunc.apply(arg2, arg1);
	}
	
	/**
	 * Curry the second argument of a function and apply the given first argument.
	 * @param biFunc The bifunction to curry.
	 * @param arg2 The second argument to pass to the curried function.
	 * @return A function that takes as input the first argument and return the bifunction result.
	 */
	public static <T, U, R> Function<T, R> curry2(BiFunction<T, U, R> biFunc, U arg2){
		return curry2(biFunc).apply(arg2);
	}
	
	/**
	 * Curry the second argument of a biconsumer.
	 * @param biCons The biconsumer to curry.
	 * @return A function that takes as input the second argument of biconsumer and return a consumer that
	 * takes the first argument.
	 */
	public static <T, U> Function<U, Consumer<T>> curry2(BiConsumer<T, U> biCons){
		return arg1 -> arg2 -> biCons.accept(arg2, arg1);
	}
	
	/**
	 * Curry the second argument of a biconsumer and apply the given first argument.
	 * @param biCons The biconsumer to curry.
	 * @param arg2 The second argument to pass to the curried biconsumer.
	 * @return A consumer that takes as input the first argument.
	 */
	public static <T, U> Consumer<T> curry2(BiConsumer<T, U> biCons, U arg2){
		return curry2(biCons).apply(arg2);
	}
}
