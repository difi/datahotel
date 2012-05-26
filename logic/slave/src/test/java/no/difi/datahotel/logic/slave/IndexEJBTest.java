package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FILE_DATASET;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import no.difi.datahotel.util.bridge.FieldLight;
import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class IndexEJBTest {

	private static String realHome;

	private Metadata metadata;

	private IndexEJB indexEJB;
	private FieldEJB fieldEJB;

	private SearchEJB search;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(IndexEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Before
	public void before() throws Exception {
		indexEJB = new IndexEJB();

		// TODO Ta over logger.

		fieldEJB = new FieldEJB();
		Field settingsFieldField = IndexEJB.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(indexEJB, fieldEJB);

		search = new SearchEJB();
		
		metadata = new Metadata();
		metadata.setLocation("difi/miljo/kalkulator");
		metadata.setUpdated(System.currentTimeMillis());
	}

	@Test
	public void testIndex() throws Exception {
		fieldEJB.update(metadata);
		indexEJB.update(metadata);
	}

	@Test
	public void testNoSource() {
		metadata.setLocation("difi/miljo/no-exists");
		
		indexEJB.update(metadata);
	}

	@Test
	public void testNoIndex() {
		Exception ex = null;

		metadata.setLocation("no/dataset/here");
		try {
			search.find(metadata, "kings", null, 1);
		} catch (Exception e) {
			ex = e;
		}

		assertNotNull(ex);
	}

	@Test
	public void testSearch() throws Exception {
		testIndex();

		assertEquals(2, search.find(metadata, "Energi", null, 1).size());
		assertEquals(0, search.find(metadata, "km", null, 1).size());
		assertEquals(1, search.find(metadata, "tog", null, 1).size());
		assertEquals(1, search.find(metadata, "ark", null, 1).size());
		assertEquals(2, search.find(metadata, "BUSS", null, 1).size());

		assertEquals(0, search.find(metadata, "Energi", null, 2).size());
		assertEquals(0, search.find(metadata, "km", null, 2).size());
		assertEquals(0, search.find(metadata, "tog", null, 2).size());
		assertEquals(0, search.find(metadata, "ark", null, 2).size());
		assertEquals(0, search.find(metadata, "BUSS", null, 2).size());
	}

	@Test
	public void testDelete() {
		indexEJB.delete("difi", "miljo", "kalkulator");
	}

	@Test
	public void testLookupAdv() throws Exception {
		metadata.setLocation("difi/geo/kommune");

		fieldEJB.update(metadata);
		indexEJB.update(metadata);

		Map<String, String> query = new HashMap<String, String>();
		query.put("kommune", "1401");
		query.put("fylke", "14");
		assertEquals(1, search.find(metadata, null, query, 1).size());
		assertEquals(0, search.find(metadata, null, query, 2).size());

		query.clear();
		query.put("kommune", "1401");
		assertEquals(1, search.find(metadata, null, query, 1).size());
		assertEquals(0, search.find(metadata, null, query, 2).size());

		query.clear();
		query.put("fylke", "14");
		assertEquals(26, search.find(metadata, "", query, 1).size());
		assertEquals(0, search.find(metadata, "", query, 2).size());

		query.clear();
		query.put("navn", "l*anger");
		assertEquals(2, search.find(metadata, "", query, 1).size());
		assertEquals(0, search.find(metadata, "", query, 2).size());

		indexEJB.delete("difi", "geo", "kommune");
	}

	@Test
	public void test10000() throws Exception {
		FieldEJB fieldEJB = Mockito.mock(FieldEJB.class);
		Field settingsFieldField = IndexEJB.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(indexEJB, fieldEJB);

		Logger logger = Mockito.mock(Logger.class);

		CSVParserFactory csvParserFactory = Mockito.mock(CSVParserFactory.class);
		Field settingsFactoryField = IndexEJB.class.getDeclaredField("csvParserFactory");
		settingsFactoryField.setAccessible(true);
		settingsFactoryField.set(indexEJB, csvParserFactory);

		CSVParser parser = Mockito.mock(CSVParser.class);

		Metadata metadata = new Metadata();
		metadata.setLocation("whoknows");
		metadata.setUpdated(10L);
		metadata.setLogger(logger);

		File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

		List<FieldLight> fields = new ArrayList<FieldLight>();
		no.difi.datahotel.util.bridge.Field field;
		field = new no.difi.datahotel.util.bridge.Field();
		field.setShortName("field1");
		field.setSearchable(false);
		fields.add(field.light());

		Map<String, String> line = new HashMap<String, String>();
		line.put("field1", "value");
		
		when(csvParserFactory.get(filename)).thenReturn(parser);
		when(fieldEJB.getFields(metadata)).thenReturn(fields);
		when(parser.hasNext()).thenReturn(true);
		when(parser.getNextLine()).thenReturn(line);
		doThrow(new RuntimeException()).when(logger).info("Document 20000");
		
		indexEJB.update(metadata);
		
		verify(parser, times(20000)).getNextLine();
		
		verify(logger).info("Building index.");
		verify(logger).info("Document 10000");
		verify(logger).info("Document 20000");
	}

	@Test
	public void testUnableToReadLine() throws Exception {
		FieldEJB fieldEJB = Mockito.mock(FieldEJB.class);
		Field settingsFieldField = IndexEJB.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(indexEJB, fieldEJB);

		Logger logger = Mockito.mock(Logger.class);

		CSVParserFactory csvParserFactory = Mockito.mock(CSVParserFactory.class);
		Field settingsFactoryField = IndexEJB.class.getDeclaredField("csvParserFactory");
		settingsFactoryField.setAccessible(true);
		settingsFactoryField.set(indexEJB, csvParserFactory);

		CSVParser parser = Mockito.mock(CSVParser.class);

		Metadata metadata = new Metadata();
		metadata.setLocation("whoknows2");
		metadata.setUpdated(10L);
		metadata.setLogger(logger);

		File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

		List<FieldLight> fields = new ArrayList<FieldLight>();
		no.difi.datahotel.util.bridge.Field field;
		field = new no.difi.datahotel.util.bridge.Field();
		field.setShortName("field1");
		field.setSearchable(false);
		fields.add(field.light());

		Map<String, String> line = new HashMap<String, String>();
		line.put("field2", "value");
		
		when(csvParserFactory.get(filename)).thenReturn(parser);
		when(fieldEJB.getFields(metadata)).thenReturn(fields);
		when(parser.hasNext()).thenReturn(true);
		when(parser.getNextLine()).thenReturn(line);
		doThrow(new RuntimeException()).when(logger).info("Document 10000");
		doThrow(new RuntimeException()).when(logger).info("[NullPointerException] Unable to index line 1. (null)");
		
		indexEJB.update(metadata);
		
		verify(parser, times(1)).getNextLine();
		verify(logger).info("Building index.");
	}
	
	@Test
	public void testUpToDate() {
		Logger logger = Mockito.mock(Logger.class);

		Metadata metadata = new Metadata();
		metadata.setLocation("difi/geo/kommune");
		metadata.setUpdated(System.currentTimeMillis());
		metadata.setLogger(logger);
		
		indexEJB.update(metadata);
		
		verify(logger).info("Building index.");
		
		indexEJB.update(metadata);
		
		verify(logger).info("Index up to date.");
	}
}
