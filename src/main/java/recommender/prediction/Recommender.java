package recommender.prediction;

import java.util.List;

public interface Recommender {
	
	public List<Integer> predict(int id, int numberOfResults);
}
