package recommender.similarities;

public interface Similarity {
	
	public double calculateSimilarity(double[] vector1, double[] vector2);
	
}
