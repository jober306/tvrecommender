package util.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * class the wraps the java 8 Function interface and is serializable
 * @author Jonathan Bergeron
 *
 * @param <T> The input of the function
 * @param <R> The output of the function
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable{}
