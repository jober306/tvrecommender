package model.recommendation;

import java.util.ArrayList;
import java.util.List;

public class Recommendations<T extends AbstractRecommendation> extends ArrayList<T>{
	
	private static final long serialVersionUID = 1L;
	
	final int userId;
	
	public Recommendations(int userId, List<T> recommendations) {
		this.userId = userId;
		addAll(recommendations);
	}
	
	public int userId() {
		return this.userId;
	}
}
