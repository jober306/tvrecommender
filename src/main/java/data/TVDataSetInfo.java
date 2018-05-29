package data;

import model.information.AbstractInformation;

public class TVDataSetInfo extends AbstractInformation {
	
	private static final long serialVersionUID = 1L;
	
	final String name;
	
	public TVDataSetInfo(String name){
		this.name = name;
	}

	@Override
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + name + "\n");
		return sb.toString();
	}
}
