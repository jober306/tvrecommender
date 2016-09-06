package recommender.prediction;

import java.util.List;

public interface Predictor {
	
	public List<Integer> predict(int userID, int numberOfResults);
}
