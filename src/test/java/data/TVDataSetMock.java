package data;


import org.apache.spark.api.java.JavaRDD;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

public class TVDataSetMock extends TVDataSet<User, TVProgram, TVEvent<User, TVProgram>> {
	
	public TVDataSetMock(JavaRDD<TVEvent<User, TVProgram>> eventsData) {
		super(eventsData);
	}
}
