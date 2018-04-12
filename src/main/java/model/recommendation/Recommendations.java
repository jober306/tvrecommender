package model.recommendation;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public class Recommendations<T extends Recommendation> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	final int userId;
	final List<T> recommendations;
	
	public Recommendations(int userId, List<T> recommendations) {
		this.userId = userId;
		this.recommendations = recommendations;
	}
	
	public int userId() {
		return this.userId;
	}
	
	public T get(int index){
		return recommendations.get(index);
	}
	
	public Stream<T> stream(){
		return recommendations.stream();
	}
	
	public int size(){
		return recommendations.size();
	}
}
