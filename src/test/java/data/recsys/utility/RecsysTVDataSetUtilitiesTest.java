package data.recsys.utility;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

public class RecsysTVDataSetUtilitiesTest {
	
	
	@Test
	public void testResourceFileExists(){
		InputStream stream = RecsysTVDataSetUtilitiesTest.class.getResourceAsStream(RecsysTVDataSetUtilities.GENRE_SUBGENRE_MAPPING_PATH);
		assertTrue(stream != null);
	}
	
	@Test
	public void testMapLoadingCorrectly(){
		assertTrue(RecsysTVDataSetUtilities.isGenreSubgenreMapNotEmpty());
	}
	
	@Test
	public void testGetGenreSubGenreMethod(){
		assertTrue(RecsysTVDataSetUtilities.getGenreName((byte)3).equals("movie"));
		assertTrue(RecsysTVDataSetUtilities.getGenreName((byte)1).equals("kids_and_music"));
		assertTrue(RecsysTVDataSetUtilities.getGenreName((byte)4).equals("society"));
		assertTrue(RecsysTVDataSetUtilities.getSubgenreName((byte) 2, (byte) 14).equals("skiing"));
		assertTrue(RecsysTVDataSetUtilities.getSubgenreName((byte) 6, (byte) 90).equals("economics"));
	}
}
