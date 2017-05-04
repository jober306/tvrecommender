package data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;

public class TVEventTest {

	LocalDateTime time = LocalDateTime.of(1990, 11, 10, 22, 34);

	TVEventMock tvEvent1 = new TVEventMock(time, 1, 2, 22, 0, 30);
	TVEventMock tvEvent2 = new TVEventMock(time, 1, 2, 22, 0, 30);
	TVEventMock tvEvent3 = new TVEventMock(time, 5, 5, 5, 5, 5);

	@Test
	public void equalsTest() {
		assertThat(tvEvent1, equalTo(tvEvent2));
		assertThat(tvEvent1, equalTo(tvEvent1));
	}

	@Test
	public void notEqualsTest() {
		assertThat(tvEvent1, not(equalTo(tvEvent3)));
	}
}
