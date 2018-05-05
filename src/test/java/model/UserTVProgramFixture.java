package model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import model.data.TVEvent;
import model.data.TVProgram;
import model.data.User;

public class UserTVProgramFixture {
	
	protected final static LocalDateTime NOW = LocalDateTime.of(2018, 4, 26, 0, 0);
	
	protected final static User user0 = new User(0);
	protected final static User user1 = new User(1);
	protected final static User user2 = new User(2);
	
	protected final static TVProgram program0 = new TVProgram(NOW, NOW, (short)0, 0);
	protected final static TVProgram program1 = new TVProgram(NOW, NOW, (short)0, 1);
	protected final static TVProgram program2 = new TVProgram(NOW, NOW, (short)0, 2);
	protected final static TVProgram program3 = new TVProgram(NOW, NOW, (short)0, 3);
	
	protected final static Set<User> allUsers = ImmutableSet.copyOf(Arrays.asList(user0, user1, user2)); 
	protected final static Set<TVProgram> allPrograms = ImmutableSet.copyOf(Arrays.asList(program0, program1, program2, program3)); 
	
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
