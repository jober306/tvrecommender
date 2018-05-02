package data;

import org.apache.spark.api.java.JavaRDD;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

/**
 * Class to obtains different probabilities given a tv data set.
 * @author Jonathan Bergeron
 *
 * @param <U> The type of user.
 * @param <P> The type of tv program.
 * @param <E> The type of tv event.
 */
public class GroundModel<U extends User, P extends TVProgram, E extends TVEvent<U, P>> {
	
	final TVDataSet<U, P, E> dataset;
	
	/**
	 * Base constructor of the class.
	 * @param dataset The tv data set.
	 */
	public GroundModel(TVDataSet<U, P, E> dataset){
		this.dataset = dataset;
	}
	
	/**
	 * Method that gives the probability that a given tv program is chosen by a random user.
	 * It approximates the probability by using maximum likelihood with the given data set.
	 * occurences of same tv program.
	 * @param tvProgram The tv program.
	 * @return The probability that the given tv program is chosen by a random user.
	 */
	public double probabilityTVProgramIsChosen(P tvProgram){
		final long totalNumberOfEvents = dataset.count();
		final Integer currentTVProgramId = tvProgram.id();
		final long numberOfEventsTVProgram = dataset.events()
			.map(E::programID)
			.filter(currentTVProgramId::equals)
			.count();
		return totalNumberOfEvents == 0 ? 0.0d : (double) numberOfEventsTVProgram / totalNumberOfEvents;
	}
	
	/**
	 * Method that gives the probability that a given tv program is chosen by a given user.
	 * It approximates the probability by using maximum likelihood with the given data set.
	 * @param tvProgram The tv program.
	 * @return The probability that the given tv program is chosen by a random user.
	 */
	public double probabilityTVProgramIsChosenByUser(P tvProgram, U user){
		JavaRDD<E> eventsRelativeToUser = dataset.events()
				.filter(event -> event.user().equals(user))
				.cache();
		final long totalNumberOfEventsRelativeToUser = eventsRelativeToUser.count();
		final long numberOfEventsTVProgramRelativeToUser = eventsRelativeToUser
			.filter(event -> event.programID() == tvProgram.id())
			.count();
		return totalNumberOfEventsRelativeToUser == 0 ? 0.0d :(double) numberOfEventsTVProgramRelativeToUser / totalNumberOfEventsRelativeToUser;
	}
	
	/**
	 * Method that gives the probability that a given tv program is known by a random user.
	 * It approximates the probability by using maximum likelihood with the given data set.
	 * @param tvProgram The tv program.
	 * @return The probability that the given tv program is known by a random user.
	 */
	public double probabilityTVProgramIsKnown(P tvProgram){
		long totalNumberOfUsers = dataset.numberOfUsers();
		long numberOfUsersThatHaveSeenTVProgram = dataset.events()
			.filter(event -> event.programID() == tvProgram.id())
			.map(E::user)
			.distinct()
			.count();
		return totalNumberOfUsers == 0 ? 0.0d : (double) numberOfUsersThatHaveSeenTVProgram / totalNumberOfUsers;
	}
	
}
