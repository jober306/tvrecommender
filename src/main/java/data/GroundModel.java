package data;

import org.apache.spark.api.java.JavaRDD;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

/**
 * Class to obtains different probabilities based on a tv data set.
 * @author Jonathan Bergeron
 *
 * @param <U> The type of user.
 * @param <P> The type of tv program.
 * @param <E> The type of tv event.
 */
public class GroundModel<U extends User, P extends TVProgram> {
	
	final TVDataSet<U, P, ? extends TVEvent<U, P>> tvDataSet;
	
	/**
	 * Base constructor of the class.
	 * @param dataset The tv data set.
	 */
	public GroundModel(TVDataSet<U, P, ?> tvDataSet){
		this.tvDataSet = tvDataSet;
	}
	
	public TVDataSet<U, P, ?> tvDataSet(){
		return tvDataSet;
	}
	
	/**
	 * Method that gives the probability that a given tv program is chosen by a random user.
	 * It approximates the probability by using maximum likelihood on the tv data set 
	 * by counting the number of times the given tv program was watched.
	 * @param tvProgram The tv program.
	 * @return The probability that the given tv program is chosen by a random user.
	 */
	public double probabilityTVProgramIsChosen(P tvProgram){
		return probabilityTVProgramIsChosen(tvProgram, 0.0d);
	}
	
	/**
	 * Method that gives the probability that a given tv program is chosen by a given user.
	 * It approximates the probability by using maximum likelihood on the tv data set 
	 * by counting the number of times the given tv program was watched by the given user.
	 * @param tvProgram The tv program.
	 * @param user The user.
	 * @return The probability that the given tv program is chosen by the given user.
	 */
	public double probabilityTVProgramIsChosenByUser(P tvProgram, U user) {
		return probabilityTVProgramIsChosenByUser(tvProgram, user, 0.0d);
	}
	
	/**
	 * Method that gives the probability that a given tv program is chosen by a random user 
	 * using additive smoothing. It approximates the probability by using maximum likelihood
	 * on the tv data set by counting the number of times the given tv program was watched.
	 * @param tvProgram The tv program.
	 * @param additiveSmoothing The additive smoothing.
	 * @return The smoothed probability that the given tv program is chosen by a random user.
	 */
	public double probabilityTVProgramIsChosen(P tvProgram, double additiveSmoothing) {
		final long totalNumberOfEvents = tvDataSet.numberOfTvEvents();
		if(totalNumberOfEvents == 0) {
			return 0.0d;
		}
		final long totalNumberOfTVProgramIds = tvDataSet.allProgramIds().size();
		final long numberOfTVEventsAboutTVProgramId = tvDataSet.tvProgramIdsCount().getOrDefault(tvProgram.id(), 0l);
		return (double) (numberOfTVEventsAboutTVProgramId + additiveSmoothing) / (totalNumberOfEvents + additiveSmoothing * totalNumberOfTVProgramIds);
	}
	
	/**
	 * Method that gives the probability that a given tv program is chosen by a given user 
	 * using additive smoothing. It approximates the probability by using maximum likelihood
	 * on the tv data set by counting the number of times the given tv program was watched by the given user.
	 * @param tvProgram The tv program.
	 * @param additiveSmoothing The additive smoothing.
	 * @return The smoothed probability that the given tv program is chosen by the given user.
	 */
	public double probabilityTVProgramIsChosenByUser(P tvProgram, U user, double additiveSmoothing){
		JavaRDD<? extends TVEvent<U, P>> eventsRelativeToUser = tvDataSet.events()
				.filter(event -> event.user().equals(user))
				.cache();
		final long totalNumberOfEventsRelativeToUser = eventsRelativeToUser.count();
		if(totalNumberOfEventsRelativeToUser == 0) {
			return 0.0d;
		}
		final long totalNumberOfTVProgramIdsRelativeToUser = eventsRelativeToUser.map(event -> event.programID()).distinct().count();
		final long numberOfEventsTVProgramIdRelativeToUser = eventsRelativeToUser
			.map(event -> event.program())
			.filter(tvProgram::equals)
			.count();
		return (double) (numberOfEventsTVProgramIdRelativeToUser + additiveSmoothing) / (totalNumberOfEventsRelativeToUser + additiveSmoothing * totalNumberOfTVProgramIdsRelativeToUser);
	}
	
	/**
	 * Method that gives the probability that a given tv program is known by a random user.
	 * It approximates the probability by using maximum likelihood with the given data set.
	 * @param tvProgram The tv program.
	 * @return The probability that the given tv program is known by a random user.
	 */
	public double probabilityTVProgramIsKnown(P tvProgram){
		long totalNumberOfUsers = tvDataSet.numberOfUsers();
		if(totalNumberOfUsers == 0) {
			return 0.0d;
		}
		long numberOfUsersThatHaveSeenTVProgram = tvDataSet.events()
			.filter(event -> event.program().equals(tvProgram))
			.map(event -> event.user())
			.distinct()
			.count();
		return (double) numberOfUsersThatHaveSeenTVProgram / totalNumberOfUsers;
	}
	
}
