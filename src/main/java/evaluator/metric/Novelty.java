package evaluator.metric;

import java.util.Set;

import data.GroundModel;
import model.data.TVProgram;
import model.data.User;
import model.recommendation.Recommendations;

public class Novelty<U extends User, P extends TVProgram> implements EvaluationMetric<U, P>{

	final GroundModel<U, P, ?> groundModel;
	
	public Novelty(GroundModel<U, P, ?> groundModel) {
		this.groundModel = groundModel;
	}
	
	@Override
	public String name() {
		return "Novelty";
	}

	@Override
	public double evaluate(Recommendations<U, P> recommendations, Set<P> groundTruth){	
		int recommendationsSize = recommendations.size();
		int numberOfTVPrograms = groundModel.tvDataSet().numberOfTvPrograms();
		double ratio = (double) numberOfTVPrograms / recommendationsSize; 
		final double log2 = Math.log(2);
		double sum = recommendations.stream()
				.mapToDouble(groundModel::probabilityTVProgramIsChosen)
				.map(p -> p * Math.log(p) / log2)
				.sum();
		return -1.0d * ratio * sum;
	}

}
