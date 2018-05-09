package data;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;
import util.spark.SparkUtilities;

public class TVDataSetFixture {
	
	static protected JavaSparkContext sc;
	
	static protected JavaRDD<TVProgram> tvProgramsRDD;
	static protected JavaRDD<TVEvent<User, TVProgram>> tvEventsRDD;
	
	static protected EPG<TVProgram> epg;
	static protected EPG<TVProgram> emptyEPG;
	
	static protected TVDataSetMock dataset;
	static protected TVDataSetMock emptyDataset;
	
	static protected EvaluationContext<User, TVProgram, TVEvent<User, TVProgram>> evaluationContext;
	
	/**
	 * Nikola Tesla was born this day!
	 */
	static protected LocalDateTime baseTime = LocalDateTime.of(1856, 7, 10, 0, 0);
	static protected LocalDateTime trainingStartTime = baseTime;
	static protected LocalDateTime trainingEndTime = baseTime.plusMinutes(106);
	static protected LocalDateTime testingStartTime = baseTime.plusMinutes(110);
	static protected LocalDateTime testingEndTime = baseTime.plusMinutes(140);

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
	
	static protected TVEvent<User, TVProgram> event1;
	static protected TVEvent<User, TVProgram> event2;
	static protected TVEvent<User, TVProgram> event3;
	static protected TVEvent<User, TVProgram> event4;
	static protected TVEvent<User, TVProgram> event5;
	static protected TVEvent<User, TVProgram> event6;
	
	static protected User user1;
	static protected User user2;
	static protected User user3;
	
	@BeforeClass
	public static void createDatatSet() {
		initializeSparkContext();
		
		initializeUser();
		
		initializeTVPrograms();
		initializeTVProgramsRDD();
		initializeEPG();
		
		initializeTVEvents();
		initializeTVEventsRDD();
		initializeTVDataset();
		
		initializeEvaluationContext();
	}
	
	static void initializeSparkContext(){
		sc = SparkUtilities.getADefaultSparkContext();
	}
	
	static void initializeUser() {
		user1 = new User(1);
		user2 = new User(2);
		user3 = new User(3);
	}

	static void initializeTVPrograms() {
		program11 = new TVProgram(baseTime, baseTime.plusHours(1), (short)1, 1);
		program13 = new TVProgram(baseTime.plusHours(1), baseTime.plusMinutes(90), (short)1, 3);
		program12 = new TVProgram(baseTime.plusMinutes(90), baseTime.plusMinutes(150), (short)1, 2);
		program23 = new TVProgram(baseTime, baseTime.plusMinutes(30), (short)2, 3);
		program22 = new TVProgram(baseTime.plusMinutes(30), baseTime.plusMinutes(90), (short)2, 2);
		program25 = new TVProgram(baseTime.plusMinutes(90), baseTime.plusMinutes(120), (short)2, 5);
		program26 = new TVProgram(baseTime.plusMinutes(120), baseTime.plusMinutes(150), (short)2, 6);
		program32 = new TVProgram(baseTime, baseTime.plusMinutes(60), (short)3, 2);
		program34 = new TVProgram(baseTime.plusMinutes(60), baseTime.plusMinutes(150), (short)3, 4);
		program44 = new TVProgram(baseTime, baseTime.plusMinutes(90), (short)4, 4);
		program46 = new TVProgram(baseTime.plusMinutes(90), baseTime.plusMinutes(120), (short)4, 6);
		program45 = new TVProgram(baseTime.plusMinutes(120), baseTime.plusMinutes(150), (short)4, 5);
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
		epg = new EPG<>(tvProgramsRDD);
		emptyEPG = new EPG<>(sc.emptyRDD());
	}
	
	private static void initializeTVEvents(){
		//watch time/program id/channel id/user id/event/duration
		event1 = new TVEvent<>(baseTime.plusMinutes(15), program11, user1, 0, 5);
		event2 = new TVEvent<>(baseTime.plusMinutes(110), program12, user2, 1, 10);
		event3 = new TVEvent<>(baseTime.plusMinutes(105), program25, user2, 2, 15);
		event4 = new TVEvent<>(baseTime.plusHours(1), program34, user2, 3, 15);
		event5 = new TVEvent<>(baseTime.plusMinutes(135), program45, user3, 4, 5);
		event6 = new TVEvent<>(baseTime, program23, user1,5, 15);
	}
	
	
	static void initializeTVEventsRDD() {
		List<TVEvent<User, TVProgram>> tvPrograms = Arrays.asList(event1, event2, event3, event4, event5, event6);
		tvEventsRDD = SparkUtilities.elementsToJavaRDD(tvPrograms, sc);
	}
	
	static void initializeTVDataset(){
		dataset = new TVDataSetMock(tvEventsRDD);
		emptyDataset = new TVDataSetMock(sc.emptyRDD());
	}
	
	static void initializeEvaluationContext() {
		evaluationContext = new EvaluationContext<>(epg, dataset, trainingStartTime, trainingEndTime, testingStartTime, testingEndTime);
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
		event1 = null;
		event2 = null;
		event3 = null;
		event4 = null;
		event5 = null;
		event6 = null;
		user1 = null;
		user2 = null;
		user3 = null;
		tvProgramsRDD = null;
		epg = null;
		emptyEPG = null;
		dataset = null;
		emptyDataset = null;
	}
}
