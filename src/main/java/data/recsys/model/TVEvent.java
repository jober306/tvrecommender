package data.recsys.model;

/**
 * TVEvent interface force the class implementing it to implement a minimum of getter methods essential to tv
 * recommendation.
 * @author Jonathan Bergeron
 *
 */
public interface TVEvent {
	
	public short getChannelID();
	public short getSlot();
	public byte getWeek();
	public byte getGenreID();
	public int getUserID();
	public int getProgramID();
	public int getEventID();
	public int getDuration();
	@Override
	public boolean equals(Object other);
	@Override
	public int hashCode();
}
