package data;

import org.apache.spark.api.java.JavaRDD;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

public class GroundModel<U extends User, P extends TVProgram, E extends TVEvent<U, P>> {
	
	final TVDataSet<U, P, E> dataset;
	
	public GroundModel(TVDataSet<U, P, E> dataset){
		this.dataset = dataset;
	}
	
	public double tvProgramIsChosenProbability(P tvProgram){
		final long totalNumberOfEvents = dataset.count();
		final Integer currentTVProgramId = tvProgram.id();
		final long numberOfEventsTVProgram = dataset.events()
			.map(E::programID)
			.filter(currentTVProgramId::equals)
			.count();
		return (double) numberOfEventsTVProgram / totalNumberOfEvents;
	}
	
	public double tvProgramIsChosenByUserProbability(P tvProgram, U user){
		JavaRDD<E> eventsRelativeToUser = dataset.events()
				.filter(event -> event.user().equals(user))
				.cache();
		final long totalNumberOfEventsRelativeToUser = eventsRelativeToUser.count();
		final long numberOfEventsTVProgramRelativeToUser = eventsRelativeToUser
			.filter(event -> event.programID() == tvProgram.id())
			.count();
		return (double) numberOfEventsTVProgramRelativeToUser / totalNumberOfEventsRelativeToUser;
	}
	
	public double tvProgramIsKnownProbability(P tvProgram){
		long totalNumberOfUsers = dataset.numberOfUsers();
		long numberOfUsersThatHaveSeenTVProgram = dataset.events()
			.filter(event -> event.programID() == tvProgram.id())
			.map(E::user)
			.distinct()
			.count();
		return (double) numberOfUsersThatHaveSeenTVProgram / totalNumberOfUsers;
	}
}
