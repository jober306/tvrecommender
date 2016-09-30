package recommender.similarities;

public class CosineSimilarity implements Similarity{
	
	private static CosineSimilarity COSINE_SIMILARITY = new CosineSimilarity();
	
	private CosineSimilarity(){};
	
	public static CosineSimilarity getInstance(){
		return COSINE_SIMILARITY;
	}
	
	@Override
	public double calculateSimilarity(double[] vector1, double[] vector2) {
		float dotProduct = 0.0f;
		float norm1 = 0.0f;
		float norm2 = 0.0f;
		for(int i = 0; i < vector1.length; i++){
			dotProduct += vector1[i] * vector2[i];
			norm1 += vector1[i]*vector1[i];
			norm2 += vector2[i]*vector2[i];
		}
		return (dotProduct / (Math.sqrt(norm1)*Math.sqrt(norm2)));
	}
}
