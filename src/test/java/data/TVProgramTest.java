package data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TVProgramTest {

	static final LocalDateTime baseTime = LocalDateTime.of(2017, 5, 2, 0, 0);

	TVProgramMock program1;
	TVProgramMock program2;
	TVProgramMock program3;

	@Before
	public void setUp() {
		initializePrograms();
	}

	private void initializePrograms() {
		program1 = new TVProgramMock(baseTime, baseTime.plusHours(1), 1, 1);
		program2 = new TVProgramMock(baseTime, baseTime.plusHours(1), 1, 1);
		program3 = new TVProgramMock(baseTime, baseTime.plusMinutes(30), 1, 1);
	}

	@Test
	public void notEqualsTest() {
		assertThat(program1, not(equalTo(program3)));
	}

	@Test
	public void equalsTest() {
		assertThat(program1, equalTo(program1));
		assertThat(program2, equalTo(program2));
	}

	@After
	public void tearDown() {
		program1 = null;
		program2 = null;
		program3 = null;
	}
}
