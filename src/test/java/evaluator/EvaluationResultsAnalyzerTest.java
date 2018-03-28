package evaluator;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
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
import evaluator.metric.IncrementMetric;
import evaluator.metric.OneMetric;
import evaluator.result.EvaluationResultsAnalyzer;
import evaluator.result.MetricResults;
import model.recommendation.Recommendation;
import model.recommendation.Recommendations;
import scala.Tuple2;

public class EvaluationResultsAnalyzerTest {
	
	static final String path = "/tv-audience-dataset/tv-audience-dataset-mock.csv";
	static Tuple2<RecsysEPG, RecsysTVDataSet> data;
	static EvaluationContext<RecsysTVProgram, RecsysTVEvent> context;
	
	EvaluationResultsAnalyzer analyzer;
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
	public void evaluatingMeanTest() {
		Recommendations<Recommendation> recommendations1 = new Recommendations<>(1, Collections.emptyList());
		Recommendations<Recommendation> recommendations2 = new Recommendations<>(2, Collections.emptyList());
		Recommendations<Recommendation> recommendations3 = new Recommendations<>(3, Collections.emptyList());

		Stream<Recommendations<Recommendation>> recommendationsStream = Stream.of(recommendations1, recommendations2, recommendations3);
		MetricResults result = incMetric.evaluate(recommendationsStream, context);
		analyzer = new EvaluationResultsAnalyzer(incMetric, result);
		double expectedMean = 2.0d;
		assertEquals(expectedMean, analyzer.means().get(incMetric), 0.0d);
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
		this.analyzer = null;
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
