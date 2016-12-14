package data.utility;

import org.apache.spark.api.java.JavaRDD;

import data.model.TVEvent;

/**
 * Class that offers some general utilities on tv data set objects.
 * @author Jonathan Bergeron
 *
 */
public class TVDataSetUtilities {
	
	/**
	 * Method that returns tv events that have at least been viewed minTimeView
	 * time.
	 * 
	 * @param minTimeView
	 *            The minimum viewing time.
	 * @return A JavaRDD of recsys tv events that have been viewed at least
	 *         minTimeView time.
	 */
	public static <T extends TVEvent> JavaRDD<T> filterByMinTimeView(JavaRDD<T> events, int minTimeView) {
		return events.filter(tvEvent -> tvEvent.getDuration() >= minTimeView);
	}
}
