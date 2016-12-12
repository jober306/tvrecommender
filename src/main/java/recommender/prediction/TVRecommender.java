package recommender.prediction;

import java.util.List;

public interface TVRecommender {
	
	public List<Integer> recommend(int week, int slot, int userId);
}
