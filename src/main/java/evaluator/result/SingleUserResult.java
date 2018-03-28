package evaluator.result;

public class SingleUserResult {
	
	final int userId;
	final double score;
	
	public SingleUserResult(int userId, double score) {
		this.userId = userId;
		this.score = score;
	}
	
	public int userId() {
		return this.userId;
	}
	
	public double score() {
		return this.score;
	}
}
