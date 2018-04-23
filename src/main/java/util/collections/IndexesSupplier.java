package util.collections;

import java.util.function.Supplier;

/**
 * An index supplier starting at 0 to infinity and beyond (if such a thing exists).
 * @author Jonathan Bergeron
 *
 */
public class IndexesSupplier implements Supplier<Integer>{
	
	int index = 0;
	
	@Override
	public Integer get() {
		return index++;
	}
}
