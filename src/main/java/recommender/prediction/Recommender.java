package recommender.prediction;

public interface Recommender {
	
	public int[] recommend(int userId, int numberOfResults);
}
