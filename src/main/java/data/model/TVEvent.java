package data.model;

import org.apache.spark.mllib.linalg.Vector;

/**
 * TVEvent interface force the class implementing it to implement a minimum of getter methods essential to tv
 * recommendation.
 * @author Jonathan Bergeron
 *
 */
public abstract class TVEvent {
	
	abstract public short getChannelID();
	abstract public short getSlot();
	abstract public byte getWeek();
	abstract public byte getGenreID();
	abstract public int getUserID();
	abstract public int getProgramID();
	abstract public int getEventID();
	abstract public int getDuration();
	abstract public Vector getProgramFeatureVector();
	@Override
	abstract public boolean equals(Object other);
	@Override
	abstract public int hashCode();
}
