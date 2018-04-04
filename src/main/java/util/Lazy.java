package util;

import java.util.function.Supplier;

/**
 * Interface used to load attributes of a class lazily.
 * @author https://stackoverflow.com/questions/29132884/lazy-field-initialization-with-lambdas/29133244#29133244
 * To do so, add the following two lines of code:
 * 
 * static <U> Supplier<U> lazily(Lazy<U> lazy) { return lazy; }
 * static <T> Supplier<T> value(T value) { return ()->value; }
 * 
 * After that simply use these methods on your attributes. For example:
 * 
 * Supplier<Baz> fieldBaz = lazily(() -> fieldBaz=value(expensiveInitBaz()));
 * Supplier<Goo> fieldGoo = lazily(() -> fieldGoo=value(expensiveInitGoo()));
 * Supplier<Eep> fieldEep = lazily(() -> fieldEep=value(expensiveInitEep()));
 * 
 * And add Getters on those suppliers:
 * 
 * public Baz getFieldBaz(){
 * 	return fieldBaz.get();
 * }
 * 
 * @param <T> The class of the attribute to load layzily.
 */
public interface Lazy<T> extends Supplier<T>{
	Supplier<T> init();
    public default T get() { return init().get(); }
}