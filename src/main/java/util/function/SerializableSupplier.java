package util.function;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * class the wraps the java 8 Supplier interface and is serializable
 * @author Jonathan Bergeron
 *
 * @param <T> The return type of the supplier.
 */
@FunctionalInterface
public interface SerializableSupplier<T> extends Supplier<T>, Serializable{}
