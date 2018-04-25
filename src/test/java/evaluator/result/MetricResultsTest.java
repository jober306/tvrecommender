package evaluator.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaSparkContext;
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
import evaluator.metric.IncrementMetric;
import evaluator.metric.OneMetric;
import model.data.User;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class MetricResultsTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static EvaluationContext<User, RecsysTVProgram, RecsysTVEvent> context;
	
	Map<Integer, Double> usersScore;
	
	OneMetric<Recommendation> oneMetric;
	IncrementMetric<Recommendation> incMetric;
	
	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		data = loader.loadDataSet();
		context = new EvaluationContext<>(data._1(), data._2(), RecsysTVDataSet.START_TIME, RecsysTVDataSet.START_TIME, RecsysTVDataSet.START_TIME, RecsysTVDataSet.START_TIME);
	}
	
	@Before
	public void setUp(){
		usersScore = new HashMap<Integer, Double>();
		usersScore.put(1, 0.5d);
		usersScore.put(2, 0.6d);
		usersScore.put(3, 0.7d);
		usersScore.put(4, 0.8d);
		this.oneMetric = new OneMetric<>();
		this.incMetric = new IncrementMetric<>();
	}
	
	@Test
	public void metricResultsInitializedWithMapTest(){
		MetricResults metricResults = new MetricResults("", usersScore);
		Map<Integer, Double> expectedResults = new HashMap<Integer, Double>(usersScore);
		assertEquals(expectedResults, metricResults.usersScore());
	}
	
	@Test
	public void metricResultsInitializedWithMapNotSeenUserTest(){
		MetricResults metricResults = new MetricResults("", usersScore);
		assertTrue(!metricResults.userScore(5).isPresent());
	}
	
	@Test
	public void evaluatingMeanTest() {
		Recommendations<Recommendation> recommendations1 = new Recommendations<>(1, Collections.emptyList());
		Recommendations<Recommendation> recommendations2 = new Recommendations<>(2, Collections.emptyList());
		Recommendations<Recommendation> recommendations3 = new Recommendations<>(3, Collections.emptyList());

		Stream<Recommendations<? extends Recommendation>> recommendationsStream = Stream.of(recommendations1, recommendations2, recommendations3);
		MetricResults result = incMetric.evaluate(recommendationsStream, context);
		double expectedMean = 2.0d;
		assertEquals(expectedMean, result.mean(), 0.0d);
	}
	
	@Test
	public void evaluatingMeanWithEmptyMetricResultsTest() {
		double expectedMean = 0.0d;
		MetricResults result = new MetricResults("", Collections.emptyMap());
		assertEquals(expectedMean, result.mean(), 0.0d);
	}
	
	@Test
	public void evaluatingGeometricMeanWithEmptyMetricResultsTest() {
		double expectedGeoMean = 0.0d;
		MetricResults result = new MetricResults("", Collections.emptyMap());
		assertEquals(expectedGeoMean, result.geometricMean(), 0.0d);
	}
	
	@Test
	public void evaluatingGeometricMeanTest() {
		Recommendations<Recommendation> recommendations1 = new Recommendations<>(1, Collections.emptyList());
		Recommendations<Recommendation> recommendations2 = new Recommendations<>(2, Collections.emptyList());
		Recommendations<Recommendation> recommendations3 = new Recommendations<>(3, Collections.emptyList());
		Stream<Recommendations<? extends Recommendation>> recommendationsStream = Stream.of(recommendations1, recommendations2, recommendations3);
		MetricResults result = incMetric.evaluate(recommendationsStream, context);
		double expectedGeoMean = 1.8171205928321d;
		assertEquals(expectedGeoMean, result.geometricMean(), 0.000001d);
	}
	
	@After
	public void tearDown(){
		usersScore = null;
		this.oneMetric = null;
		this.incMetric = null;
	}
	
	@AfterClass
	public static void tearDownOnce() {
		context.close();
		sc.close();
	}
}
