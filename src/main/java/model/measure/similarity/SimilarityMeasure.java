package model.measure.similarity;

import model.measure.Measure;

/**
 * Interface that represents a similarity measure. It only carries semantic meaning.
 * Class implementing it are expected to meet those two conditions:
 * 
 * 1. The measure should be symmetric, i.e. the order of vector parameters must not matter.
 * 2. The measure must return 1.0d for vectors with identical values.
 * 
 * \\TODO: Consider using exception when those requirements are not met.
 * @author Jonathan Bergeron
 *
 */
public interface SimilarityMeasure extends Measure{}
