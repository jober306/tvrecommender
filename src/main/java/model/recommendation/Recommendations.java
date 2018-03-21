package model.recommendation;

import java.util.List;
import java.util.stream.Stream;

public class Recommendations<T extends AbstractRecommendation> {
	
	final int userId;
	final List<T> recommendations;
	
	public Recommendations(int userId, List<T> recommendations) {
		this.userId = userId;
		this.recommendations = recommendations;
	}
	
	public int userId() {
		return this.userId;
	}
	
	public T get(int index) {
		return this.recommendations.get(index);
	}
	
	public List<T> get(){
		return this.recommendations;
	}
	
	public Stream<T> stream() {
		return this.recommendations.stream();
	}
	
	public int size() {
		return this.recommendations.size();
	}
}
