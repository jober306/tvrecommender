package recommender;

import java.util.Map;

import evaluator.information.Information;

public final class RecommenderInfo implements Information{
	
	final String name;
	final Map<String, String> parameters;
	
	public RecommenderInfo(String name, Map<String, String> parameters){
		this.name = name;
		this.parameters = parameters;
	}

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ");
		sb.append(name);
		sb.append("\nParameters\n");
		parameters.entrySet().forEach(parameter -> sb.append(parameter.getKey() + ": " + parameter.getValue() + "\n"));
		return sb.toString();
	}
}
