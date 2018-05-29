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
import data.recsys.utility.RecsysUtilities;
import evaluator.metric.IncrementMetric;
import evaluator.metric.OneMetric;
import model.data.User;
import model.recommendation.Recommendations;
import scala.Tuple2;
import util.spark.SparkUtilities;

public class MetricResultsTest {

	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	
	static JavaSparkContext sc;
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static EvaluationContext<User, RecsysTVProgram, RecsysTVEvent> context;
	
	Map<User, Double> usersScore;
	
	OneMetric<User, RecsysTVProgram> oneMetric;
	IncrementMetric<User, RecsysTVProgram> incMetric;
	
	@BeforeClass
	public static void setUpOnce() {
		sc = SparkUtilities.getADefaultSparkContext();
		RecsysTVDataSetLoader loader = new RecsysTVDataSetLoader(path, sc);
		data = loader.loadDataSet();
		context = new EvaluationContext<>(data._1(), data._2(), RecsysUtilities.START_TIME, RecsysUtilities.START_TIME, RecsysUtilities.START_TIME, RecsysUtilities.START_TIME);
	}
	
	@Before
	public void setUp(){
		usersScore = new HashMap<User, Double>();
		usersScore.put(new User(1), 0.5d);
		usersScore.put(new User(2), 0.6d);
		usersScore.put(new User(3), 0.7d);
		usersScore.put(new User(4), 0.8d);
		this.oneMetric = new OneMetric<>();
		this.incMetric = new IncrementMetric<>();
	}
	
	@Test
	public void metricResultsInitializedWithMapTest(){
		MetricResults<User> metricResults = new MetricResults<>("", usersScore);
		Map<User, Double> expectedResults = new HashMap<User, Double>(usersScore);
		assertEquals(expectedResults, metricResults.userScores());
	}
	
	@Test
	public void metricResultsInitializedWithMapNotSeenUserTest(){
		MetricResults<User> metricResults = new MetricResults<>("", usersScore);
		assertTrue(!metricResults.userScores.containsKey(new User(5)));
	}
	
	@Test
	public void evaluatingMeanTest() {
		Recommendations<User, RecsysTVProgram> recommendations1 = new Recommendations<>(new User(1), Collections.emptyList());
		Recommendations<User, RecsysTVProgram> recommendations2 = new Recommendations<>(new User(2), Collections.emptyList());
		Recommendations<User, RecsysTVProgram> recommendations3 = new Recommendations<>(new User(3), Collections.emptyList());

		Stream<Recommendations<User, RecsysTVProgram>> recommendationsStream = Stream.of(recommendations1, recommendations2, recommendations3);
		MetricResults<User> result = incMetric.evaluate(recommendationsStream, context);
		double expectedMean = 2.0d;
		assertEquals(expectedMean, result.mean(), 0.0d);
	}
	
	@Test
	public void evaluatingMeanWithEmptyMetricResultsTest() {
		double expectedMean = 0.0d;
		MetricResults<User> result = new MetricResults<>("", Collections.emptyMap());
		assertEquals(expectedMean, result.mean(), 0.0d);
	}
	
	@Test
	public void varianceTest() {
		MetricResults<User> metricResults = new MetricResults<>("", usersScore);
		double expectedResult = 0.0125d;
		double actualResult = metricResults.variance();
		assertEquals(expectedResult, actualResult, 0.0001);
	}
	
	@Test
	public void varianceEmtpyResultsTest() {
		MetricResults<User> metricResults = new MetricResults<>("", Collections.emptyMap());
		double expectedResult = 0.0d;
		double actualResult = metricResults.variance();
		assertEquals(expectedResult, actualResult, 0.0d);
	}
	
	@Test
	public void evaluatingGeometricMeanWithEmptyMetricResultsTest() {
		double expectedGeoMean = 0.0d;
		MetricResults<User> result = new MetricResults<>("", Collections.emptyMap());
		assertEquals(expectedGeoMean, result.geometricMean(), 0.0d);
	}
	
	@Test
	public void evaluatingGeometricMeanTest() {
		Recommendations<User, RecsysTVProgram> recommendations1 = new Recommendations<>(new User(1), Collections.emptyList());
		Recommendations<User, RecsysTVProgram> recommendations2 = new Recommendations<>(new User(2), Collections.emptyList());
		Recommendations<User, RecsysTVProgram> recommendations3 = new Recommendations<>(new User(3), Collections.emptyList());
		Stream<Recommendations<User, RecsysTVProgram>> recommendationsStream = Stream.of(recommendations1, recommendations2, recommendations3);
		MetricResults<User> result = incMetric.evaluate(recommendationsStream, context);
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
		sc.close();
	}
}
