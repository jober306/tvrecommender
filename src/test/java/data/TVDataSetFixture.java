package data;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import util.spark.SparkUtilities;

public class TVDataSetFixture {
	
	static protected JavaSparkContext sc;
	
	static protected JavaRDD<TVProgram> tvProgramsRDD;
	static protected JavaRDD<TVEvent> tvEventsRDD;
	
	static protected EPG<TVProgram> epg;
	static protected EPG<TVProgram> emptyEPG;
	
	static protected TVDataSetMock dataset;
	static protected TVDataSetMock emptyDataset;
	
	/**
	 * Nikola Tesla was born this day!
	 */
	static protected LocalDateTime baseTime = LocalDateTime.of(1856, 7, 10, 0, 0);

	static protected TVProgram program11;
	static protected TVProgram program13;	
	static protected TVProgram program12;
	static protected TVProgram program23;
	static protected TVProgram program22;
	static protected TVProgram program25;
	static protected TVProgram program26;
	static protected TVProgram program32;
	static protected TVProgram program34;
	static protected TVProgram program44;
	static protected TVProgram program46;
	static protected TVProgram program45;
	
	static protected TVEvent event1;
	static protected TVEvent event2;
	static protected TVEvent event3;
	static protected TVEvent event4;
	static protected TVEvent event5;
	static protected TVEvent event6;
	
	@BeforeClass
	public static void createDatatSet() {
		initializeSparkContext();
		
		initializeTVPrograms();
		initializeTVProgramsRDD();
		initializeEPG();
		
		initializeTVEvents();
		initializeTVEventsRDD();
		initializeTVDataset();
	}
	
	static void initializeSparkContext(){
		sc = SparkUtilities.getADefaultSparkContext();
	}

	static void initializeTVPrograms() {
		program11 = new TVProgram(baseTime, baseTime.plusHours(1), 1, 1);
		program13 = new TVProgram(baseTime.plusHours(1), baseTime.plusMinutes(90), 1, 3);
		program12 = new TVProgram(baseTime.plusMinutes(90), baseTime.plusMinutes(150), 1, 2);
		program23 = new TVProgram(baseTime, baseTime.plusMinutes(30), 2, 3);
		program22 = new TVProgram(baseTime.plusMinutes(30), baseTime.plusMinutes(90), 2, 2);
		program25 = new TVProgram(baseTime.plusMinutes(90), baseTime.plusMinutes(120), 2, 5);
		program26 = new TVProgram(baseTime.plusMinutes(120), baseTime.plusMinutes(150), 2, 6);
		program32 = new TVProgram(baseTime, baseTime.plusMinutes(60), 3, 2);
		program34 = new TVProgram(baseTime.plusMinutes(60), baseTime.plusMinutes(150), 3, 4);
		program44 = new TVProgram(baseTime, baseTime.plusMinutes(90), 4, 4);
		program46 = new TVProgram(baseTime.plusMinutes(90), baseTime.plusMinutes(120), 4, 6);
		program45 = new TVProgram(baseTime.plusMinutes(120), baseTime.plusMinutes(150), 4, 5);
	}
	
	static void initializeTVProgramsRDD() {
		List<TVProgram> tvPrograms = new ArrayList<TVProgram>();
		tvPrograms.add(program11);
		tvPrograms.add(program13);
		tvPrograms.add(program12);
		tvPrograms.add(program23);
		tvPrograms.add(program22);
		tvPrograms.add(program25);
		tvPrograms.add(program26);
		tvPrograms.add(program32);
		tvPrograms.add(program34);
		tvPrograms.add(program44);
		tvPrograms.add(program46);
		tvPrograms.add(program45);
		tvProgramsRDD = SparkUtilities.elementsToJavaRDD(tvPrograms, sc);
	}
	
	static void initializeEPG(){
		epg = new EPG<>(tvProgramsRDD, sc);
		emptyEPG = new EPG<>(sc.emptyRDD(), sc);
	}
	
	private static void initializeTVEvents(){
		//watch time/program id/channel id/user id/event/duration
		event1 = new TVEvent(baseTime.plusMinutes(15), program11, 1, 0, 5);
		event2 = new TVEvent(baseTime.plusMinutes(110), program12, 2, 1, 10);
		event3 = new TVEvent(baseTime.plusMinutes(105), program25, 2, 2, 15);
		event4 = new TVEvent(baseTime.plusHours(1), program34, 2, 3, 15);
		event5 = new TVEvent(baseTime.plusMinutes(135), program45, 3, 4, 5);
		event6 = new TVEvent(baseTime, program23,1,5, 15);
	}
	
	
	static void initializeTVEventsRDD() {
		List<TVEvent> tvPrograms = Arrays.asList(event1, event2, event3, event4, event5, event6);
		tvEventsRDD = SparkUtilities.elementsToJavaRDD(tvPrograms, sc);
	}
	
	static void initializeTVDataset(){
		dataset = new TVDataSetMock(tvEventsRDD, sc);
		emptyDataset = new TVDataSetMock(sc.emptyRDD(), sc);
	}

	@AfterClass
	public static void destroyDataSet() {
		sc.stop();
		sc = null;
		program11 = null;
		program13 = null;
		program12 = null;
		program23 = null;
		program22 = null;
		program25 = null;
		program26 = null;
		program32 = null;
		program34 = null;
		program44 = null;
		program46 = null;
		program45 = null;
		tvProgramsRDD = null;
		epg = null;
		emptyEPG = null;
		dataset = null;
		emptyDataset = null;
	}
}
