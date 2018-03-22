package evaluator.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.EvaluationContext;
import data.recsys.RecsysEPG;
import data.recsys.RecsysTVDataSet;
import data.recsys.RecsysTVEvent;
import data.recsys.RecsysTVProgram;
import data.recsys.loader.RecsysTVDataSetLoader;
import evaluator.result.EvaluationResult;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import scala.Tuple2;

public class AbstractEvaluationMetricTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static final int USER_ID = 10;
	static EvaluationContext<RecsysTVProgram, RecsysTVEvent> context;
	
	OneMetric<Recommendation> oneMetric;
	IncrementMetric<Recommendation> incMetric;
	
	@BeforeClass
	public static void setUpOnce() {
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path);
		data = loader.loadDataSet();
		context = new EvaluationContext<>(data._1(), data._2(), RecsysTVDataSet.START_TIME, RecsysTVDataSet.START_TIME);

	}
	
	@Before
	public void setUp() {
		this.oneMetric = new OneMetric<>();
		this.incMetric = new IncrementMetric<>();
	}
	
	@Test
	public void listOfResultsIsEmptyTest() {
		assertTrue(oneMetric.results().isEmpty());
	}
	
	@Test
	public void evaluatingARecommendationsAddResultToListTest() {
		Recommendations<Recommendation> recommendations = new Recommendations<>(USER_ID, Collections.emptyList());
		EvaluationResult result = oneMetric.evaluate(recommendations, context);
		
		int expectedResultsSize = 1;
		assertEquals(expectedResultsSize, oneMetric.results().size());
		assertEquals(result, oneMetric.results().get(0));
	}
	
	@Test
	public void evaluatingStreamRecommendationsAddResultToListTest() {
		Recommendations<Recommendation> recommendations = new Recommendations<>(USER_ID, Collections.emptyList());
		Stream<Recommendations<Recommendation>> recommendationsStream = Stream.of(recommendations, recommendations, recommendations);
		List<EvaluationResult> results = oneMetric.evaluate(recommendationsStream, context);
		
		int expectedResultsSize = 3;
		assertEquals(expectedResultsSize, oneMetric.results().size());
		assertEquals(results, oneMetric.results());
	}
	
	@Test
	public void evaluatingRecommendationsWithGroundTruthTest() {
		Recommendations<Recommendation> recommendations = new Recommendations<>(USER_ID, Collections.emptyList());
		EvaluationResult result = oneMetric.evaluate(recommendations, Collections.emptyList());
		
		int expectedResultsSize = 1;
		assertEquals(expectedResultsSize, oneMetric.results().size());
		assertEquals(result, oneMetric.results().get(0));
	}
	
	@Test
	public void evaluatingMeanTest() {
		Recommendations<Recommendation> recommendations = new Recommendations<>(USER_ID, Collections.emptyList());
		Stream<Recommendations<Recommendation>> recommendationsStream = Stream.of(recommendations, recommendations, recommendations);
		incMetric.evaluate(recommendationsStream, context);
		
		double expectedMean = 2.0d;
		assertEquals(expectedMean, incMetric.mean(), 0.0d);
	}
	
	@Test
	public void evaluatingMeanWithEmptyMetricResultsTest() {
		double expectedMean = 0.0d;
		assertEquals(expectedMean, oneMetric.mean(), 0.0d);
	}
	
	@Test
	public void evaluatingGeometricMeanWithEmptyMetricResultsTest() {
		double expectedGeoMean = 0.0d;
		assertEquals(expectedGeoMean, oneMetric.geometricMean(), 0.0d);
	}
	
	@Test
	public void evaluatingGeometricMeanTest() {
		Recommendations<Recommendation> recommendations = new Recommendations<>(USER_ID, Collections.emptyList());
		Stream<Recommendations<Recommendation>> recommendationsStream = Stream.of(recommendations, recommendations, recommendations);
		incMetric.evaluate(recommendationsStream, context);
		
		double expectedGeoMean = 1.8171205928321d;
		assertEquals(expectedGeoMean, incMetric.geometricMean(), 0.000001d);
	}
	
	@After
	public void tearDown() {
		this.oneMetric = null;
		this.incMetric = null;
	}
	
	@AfterClass
	public static void tearDownOnce() {
		RecsysTVDataSet testSet = (RecsysTVDataSet) context.getTestSet();
		testSet.closeMap();
		RecsysTVDataSet dataSet = data._2();
		dataSet.close();
	}
}
