package evaluator.metric;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;

import data.TVProgramMock;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;

/**
 * Fixtrue class that will configure a default recommendations
 * @author Jonathan Bergeron
 *
 */
public abstract class RecommendationsFixture {
	
	static final LocalDateTime time = LocalDateTime.of(2018, 03, 21, 16, 6);
	
	static final Recommendation rec1 = new Recommendation(new TVProgramMock(time, time, 0, 1));
	static final Recommendation rec2 = new Recommendation(new TVProgramMock(time, time, 0, 2));
	static final Recommendation rec3 = new Recommendation(new TVProgramMock(time, time, 0, 3));
	static final Recommendation rec4 = new Recommendation(new TVProgramMock(time, time, 0, 4));
	static final Recommendation rec5= new Recommendation(new TVProgramMock(time, time, 0, 2));
	static final Recommendation rec6 = new Recommendation(new TVProgramMock(time, time, 0, 3));
	static final Recommendation rec7 = new Recommendation(new TVProgramMock(time, time, 0, 4));
	static final Recommendation rec8 = new Recommendation(new TVProgramMock(time, time, 0, 3));
	static final Recommendation rec9 = new Recommendation(new TVProgramMock(time, time, 0, 4));
	static final Recommendation rec10 = new Recommendation(new TVProgramMock(time, time, 0, 4));
	
	
	protected Recommendations<Recommendation> distinctRecommendations;
	protected Recommendations<Recommendation> allRecommendations;
	protected Recommendations<Recommendation> sameRecommendations;
	
	@Before
	public void setUp() {
		distinctRecommendations = new Recommendations<>(1, Arrays.asList(rec1, rec2, rec3, rec4));
		allRecommendations = new Recommendations<>(2, Arrays.asList(rec1, rec2, rec3, rec4, rec5, rec6, rec7, rec8, rec9, rec10));
		sameRecommendations = new Recommendations<>(3, Arrays.asList(rec1, rec1, rec1 ,rec1));
	}
	
	@After
	public void tearDown() {
		distinctRecommendations = null;
		allRecommendations = null;
		sameRecommendations = null;
	}
}
