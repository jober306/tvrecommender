package data.recsys.mapper;

/**
 * Interface that indicates whether or not a class (typically a tv data set) has its ids mapped.
 * This interface force the class implementing it to offer method to map original user/program/event
 * id to mapped user/program/event id and vice versa.
 * @author Jonathan Bergeron
 *
 */
public interface IDMap {
	public int getOriginalUserID(int mappedID);
	public int getOriginalProgramID(int mappedID);
	public int getOriginalEventID(int mappedID);
	public int getMappedUserID(int userID);
	public int getMappedProgramID(int programID);
	public int getMappedEventID(int eventID);
}
