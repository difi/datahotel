package no.difi.datahotel.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;


import no.difi.datahotel.BaseTest;

import org.junit.Test;

public class CSVReaderTest extends BaseTest {

	@SuppressWarnings("deprecation")
	@Test
	public void readArraySemi() throws Exception {
		File file = new File(this.getClass().getResource("/csv/simple-semikolon.csv").getFile());
		CSVReader parser = new CSVReader(file);
		String[] row;

		assertEquals(new String[] { "id", "name", "age" }, parser.getHeaders());

		assertTrue(parser.hasNext());

		row = parser.getNextLineArray();
		assertEquals("Ole", row[1]);
		assertEquals("1", row[0]);
		assertEquals("3", row[2]);

		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());

		row = parser.getNextLineArray();
		assertEquals("Nils", row[1]);
		assertEquals("3", row[0]);
		assertEquals("6", row[2]);

		assertFalse(parser.hasNext());

		parser.close();
	}

	@Test
	public void testHasNextError() throws Exception {
		File file = new File(this.getClass().getResource("/csv/simple-semikolon.csv").getFile());
		CSVReader parser = new CSVReader(file);

		assertTrue(parser.hasNext());

		Field rField = CSVReader.class.getDeclaredField("csvReader");
		rField.setAccessible(true);
		rField.set(parser, null);

		assertFalse(parser.hasNext());
	}
}
