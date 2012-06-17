package no.difi.datahotel.util.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CSVParserImplTwoTest {
	@SuppressWarnings("deprecation")
	@Test
	public void readMap() throws Exception {
		CSVMetainfo metainfo = new CSVMetainfo(',', true, true);
		File file = new File(this.getClass().getResource("/csv/simple-komma.csv")
				.getFile());
		CSVParser parser = new CSVParserImpl(file, metainfo);
		Map<String, String> row;

		assertEquals(new String[] { "id", "name", "age" }, parser.getHeaders());

		assertTrue(parser.hasNext());

		row = parser.getNextLine();
		assertEquals("Ole", row.get("name"));
		assertEquals("1", row.get("id"));
		assertEquals("3", row.get("age"));

		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());

		row = parser.getNextLine();
		assertEquals("Nils", row.get("name"));
		assertEquals("3", row.get("id"));
		assertEquals("6", row.get("age"));

		assertFalse(parser.hasNext());

		assertEquals(metainfo.getDelimiter(), parser.getDelimiter());

		parser.close();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void readArray() throws Exception {
		CSVMetainfo metainfo = new CSVMetainfo(',', true, true);
		File file = new File(this.getClass().getResource("/csv/simple-komma.csv")
				.getFile());
		CSVParser parser = new CSVParserImpl(file, metainfo);
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

		assertEquals(metainfo.getDelimiter(), parser.getDelimiter());

		parser.close();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void readArraySemi() throws Exception {
		File file = new File(this.getClass()
				.getResource("/csv/simple-semikolon.csv").getFile());
		CSVParser parser = CSVParserFactory.getCSVParser(file);
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

		assertEquals(';', parser.getDelimiter());

		parser.close();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void readHeadless() throws Exception {
		File file = new File(this.getClass()
				.getResource("/csv/simple-headless.csv").getFile());
		CSVMetainfo metainfo = new CSVMetainfo();

		assertNull(metainfo.getQuoted());

		metainfo.setDelimiter(';');
		metainfo.setHeader(false);
		metainfo.setQuoted(true);

		CSVParser parser = new CSVParserImpl(file, metainfo);

		assertNull(parser.getHeaders());

		assertTrue(parser.hasNext());

		assertEquals(new String[] { "0", "1", "2" }, parser.getHeaders());
		assertEquals(new String[] { "1", "Ole", "3" },
				parser.getNextLineArray());

		assertTrue(parser.hasNext());
		assertTrue(parser.hasNext());
		assertFalse(parser.hasNext());
	}
	
	@Test
	public void testReadTwoFirstLines() throws Exception
	{
		File file = new File(this.getClass()
				.getResource("/csv/simple-semikolon.csv").getFile());
		CSVParser parser = CSVParserFactory.getCSVParser(file);
		
		List<String[]> result = parser.getTwoFirstLines();
		
		assertEquals(2, result.size());
		assertEquals(3, result.get(0).length);
		assertEquals(3, result.get(1).length);
	}
	
	@Test
	public void testHasNextError() throws Exception
	{
		File file = new File(this.getClass()
				.getResource("/csv/simple-semikolon.csv").getFile());
		CSVParser parser = CSVParserFactory.getCSVParser(file);
		
		assertTrue(parser.hasNext());
		
		Field rField = CSVParserImpl.class.getDeclaredField("reader");
		rField.setAccessible(true);
		rField.set(parser, null);
		
		assertFalse(parser.hasNext());
	}
}
