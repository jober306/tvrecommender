package data;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class EPGMock extends EPG<TVProgramMock> {

	private static final long serialVersionUID = 1L;

	public EPGMock(JavaRDD<TVProgramMock> electronicProgrammingGuide,
			JavaSparkContext sc) {
		super(electronicProgrammingGuide, sc);
	}
}
