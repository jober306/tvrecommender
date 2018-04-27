package model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

public class UserTVProgramFixture {
	
	protected final static LocalDateTime NOW = LocalDateTime.of(2018, 4, 26, 0, 0);
	
	protected final static User user0 = new User(0);
	protected final static User user1 = new User(1);
	protected final static User user2 = new User(2);
	
	protected final static TVProgram program0 = new TVProgram(NOW, NOW, 0, 0);
	protected final static TVProgram program1 = new TVProgram(NOW, NOW, 0, 1);
	protected final static TVProgram program2 = new TVProgram(NOW, NOW, 0, 2);
	protected final static TVProgram program3 = new TVProgram(NOW, NOW, 0, 3);
	
	protected final static Map<User, Integer> userMapping; 
	static {
		Map<User, Integer> map = new HashMap<User, Integer>();
		map.put(user0, 0);
		map.put(user1, 1);
		map.put(user2, 2);
		userMapping = map;
	};
	protected final static Map<TVProgram, Integer> programMapping;
	static {
		Map<TVProgram, Integer> map = new HashMap<TVProgram, Integer>();
		map.put(program0, 0);
		map.put(program1, 1);
		map.put(program2, 2);
		map.put(program3, 3);
		programMapping = map;
	}
	
	protected final static TVEvent<User, TVProgram> event1 = new TVEvent<>(NOW, program0, user1, 0, 0);
	protected final static TVEvent<User, TVProgram> event2 = new TVEvent<>(NOW, program0, user2, 0, 0);
	protected final static TVEvent<User, TVProgram> event3 = new TVEvent<>(NOW, program1, user0, 0, 0);
	protected final static TVEvent<User, TVProgram> event4 = new TVEvent<>(NOW, program1, user2, 0, 0);
	protected final static TVEvent<User, TVProgram> event5 = new TVEvent<>(NOW, program3, user0, 0, 0);
	protected final static TVEvent<User, TVProgram> event6 = new TVEvent<>(NOW, program3, user1, 0, 0);



	protected static final int NUM_ROWS = 3;
	protected static final int NUM_COL = 4;
	protected static final double[] MATRIX_VALUES = {0,1,4,3,0,2,0,0,0,1,3,0};
	protected static final double[] SPARSE_MATRIX_VALUES = {1,4,3,2,1,3};
	protected static final int[] COL_PTRS = {0,2,4,4,6};
	protected static final int[] ROW_INDICES = {1,2,0,2,0,1};
	protected static final int[][] ROW_VALUE_INDICES = {{1,3}, {0,3},{0,1}};
	protected static final double[][] ROW_SPARSE_MATRIX_VALUES = {{3,1},{1,3},{4,2}};
	
}
