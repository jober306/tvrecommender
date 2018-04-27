package evaluator.metric;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;

import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Fixtrue class that will configure a default recommendations
 * @author Jonathan Bergeron
 *
 */
public abstract class RecommendationsFixture {
	
	static final LocalDateTime time = LocalDateTime.of(2018, 03, 21, 16, 6);
	
	protected static final TVProgram tvShow1 = new TVProgram(time, time, 0, 1);
	protected static final TVProgram tvShow2 = new TVProgram(time, time, 0, 2);
	protected static final TVProgram tvShow3 = new TVProgram(time, time, 0, 3);
	protected static final TVProgram tvShow4 = new TVProgram(time, time, 0, 4);
	protected static final TVProgram tvShow5= new TVProgram(time.plusHours(1), time.plusHours(2), 0, 2);
	protected static final TVProgram tvShow6 = new TVProgram(time, time.plusHours(1), 0, 3);
	protected static final TVProgram tvShow7 = new TVProgram(time, time, 0, 4);
	protected static final TVProgram tvShow8 = new TVProgram(time.plusHours(1), time.plusHours(2), 0, 3);
	protected static final TVProgram tvShow9 = new TVProgram(time, time, 0, 4);
	protected static final TVProgram tvShow10 = new TVProgram(time, time, 0, 4);
	protected static final TVProgram tvShow11 = new TVProgram(time, time, 0,5);
	protected static final TVProgram tvShow12 = new TVProgram(time, time, 0,6);
	protected static final TVProgram tvShow13 = new TVProgram(time, time, 0,7);
	protected static final TVProgram tvShow14 = new TVProgram(time, time, 0,8);
	protected static final TVProgram tvShow15 = new TVProgram(time, time, 0,9);
	
	static final Recommendation rec1 = new Recommendation(tvShow1);
	static final Recommendation rec2 = new Recommendation(tvShow2);
	static final Recommendation rec3 = new Recommendation(tvShow3);
	static final Recommendation rec4 = new Recommendation(tvShow4);
	static final Recommendation rec5= new Recommendation(tvShow5);
	static final Recommendation rec6 = new Recommendation(tvShow6);
	static final Recommendation rec7 = new Recommendation(tvShow7);
	static final Recommendation rec8 = new Recommendation(tvShow8);
	static final Recommendation rec9 = new Recommendation(tvShow9);
	static final Recommendation rec10 = new Recommendation(tvShow10);


	
	
	
	protected Recommendations<User, Recommendation> distinctRecommendations;
	protected Recommendations<User, Recommendation> allRecommendations;
	protected Recommendations<User, Recommendation> sameRecommendations;
	
	@Before
	public void setUp() {
		distinctRecommendations = new Recommendations<>(new User(1), Arrays.asList(rec1, rec2, rec3, rec4));
		allRecommendations = new Recommendations<>(new User(2), Arrays.asList(rec1, rec2, rec3, rec4, rec5, rec6, rec7, rec8, rec9, rec10));
		sameRecommendations = new Recommendations<>(new User(3), Arrays.asList(rec1, rec1, rec1 ,rec1));
	}
	
	@After
	public void tearDown() {
		distinctRecommendations = null;
		allRecommendations = null;
		sameRecommendations = null;
	}
}
