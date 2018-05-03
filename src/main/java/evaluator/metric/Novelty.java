package evaluator.metric;

import java.util.Set;

import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

public class Novelty<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{

	@Override
	public String name() {
		return "Novelty";
	}

	@Override
	public double evaluate(Recommendations<U, P> recommendations, Set<P> groundTruth){	
		return 0;
	}

}
