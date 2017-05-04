package data.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TVDataSetTest {

	@Test
	public void newInstanceTest() {
		TVDataSetMock dataSetMock = new TVDataSetMock(null, null);
		TVDataSet<TVEventMock> dataSet = (TVDataSet<TVEventMock>) dataSetMock;
		TVDataSet<TVEventMock> newDataSet = dataSet.newInstance(null, null);
		assertThat(dataSetMock.mockInitialized, equalTo(true));
		assertThat(newDataSet, instanceOf(TVDataSetMock.class));
		assertThat(((TVDataSetMock) newDataSet).mockInitialized, equalTo(true));
	}
}
