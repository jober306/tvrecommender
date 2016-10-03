package recommender.prediction;

import java.util.List;

public interface Predictor {
	
	public List<Integer> predict(int id, int numberOfResults);
}
