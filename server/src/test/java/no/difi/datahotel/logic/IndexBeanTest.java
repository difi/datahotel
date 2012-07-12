package no.difi.datahotel.logic;

import static no.difi.datahotel.util.Filesystem.FILE_DATASET;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import no.difi.datahotel.logic.FieldBean;
import no.difi.datahotel.logic.IndexBean;
import no.difi.datahotel.logic.SearchBean;
import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class IndexBeanTest {

	private static String realHome;

	private Metadata metadata;

	private IndexBean indexBean;
	private FieldBean fieldBean;
	private SearchBean searchBean;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(IndexBeanTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Before
	public void before() throws Exception {
		indexBean = new IndexBean();

		// TODO Ta over logger.

		fieldBean = new FieldBean();
		Field settingsFieldField = IndexBean.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(indexBean, fieldBean);

		searchBean = new SearchBean();
		
		metadata = new Metadata();
		metadata.setLocation("difi/miljo/kalkulator");
		metadata.setUpdated(System.currentTimeMillis());
	}

	@Test
	public void testIndex() throws Exception {
		fieldBean.update(metadata);
		indexBean.update(metadata);
		searchBean.update(metadata);
	}

	@Test
	public void testNoSource() {
		metadata.setLocation("difi/miljo/no-exists");
		
		indexBean.update(metadata);
		searchBean.update(metadata);
	}

	@Test
	public void testNoIndex() {
		metadata.setLocation("no/dataset/here");
		assertEquals(0, searchBean.find(metadata, "kings", null, 1).getEntries().size());
	}

	@Test
	public void testSearch() throws Exception {
		testIndex();

		assertEquals(2, searchBean.find(metadata, "Energi", null, 1).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "km", null, 1).getEntries().size());
		assertEquals(1, searchBean.find(metadata, "tog", null, 1).getEntries().size());
		assertEquals(1, searchBean.find(metadata, "ark", null, 1).getEntries().size());
		assertEquals(2, searchBean.find(metadata, "BUSS", null, 1).getEntries().size());

		assertEquals(0, searchBean.find(metadata, "Energi", null, 2).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "km", null, 2).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "tog", null, 2).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "ark", null, 2).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "BUSS", null, 2).getEntries().size());
	}

	@Test
	public void testDelete() {
		indexBean.delete("difi/miljo/kalkulator");
	}

	@Test
	public void testLookupAdv() throws Exception {
		metadata.setLocation("difi/geo/kommune");

		fieldBean.update(metadata);
		indexBean.update(metadata);
		searchBean.update(metadata);

		Map<String, String> query = new HashMap<String, String>();
		query.put("kommune", "1401");
		query.put("fylke", "14");
		assertEquals(1, searchBean.find(metadata, null, query, 1).getEntries().size());
		assertEquals(0, searchBean.find(metadata, null, query, 2).getEntries().size());

		query.clear();
		query.put("kommune", "1401");
		assertEquals(1, searchBean.find(metadata, null, query, 1).getEntries().size());
		assertEquals(0, searchBean.find(metadata, null, query, 2).getEntries().size());

		query.clear();
		query.put("fylke", "14");
		assertEquals(26, searchBean.find(metadata, "", query, 1).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "", query, 2).getEntries().size());

		query.clear();
		query.put("navn", "l*anger");
		assertEquals(2, searchBean.find(metadata, "", query, 1).getEntries().size());
		assertEquals(0, searchBean.find(metadata, "", query, 2).getEntries().size());

		indexBean.delete("difi/geo/kommune");
	}

	@Test
	public void test10000() throws Exception {
		FieldBean fieldEJB = Mockito.mock(FieldBean.class);
		Field settingsFieldField = IndexBean.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(indexBean, fieldEJB);

		Logger logger = Mockito.mock(Logger.class);

		CSVParserFactory csvParserFactory = Mockito.mock(CSVParserFactory.class);
		Field settingsFactoryField = IndexBean.class.getDeclaredField("csvParserFactory");
		settingsFactoryField.setAccessible(true);
		settingsFactoryField.set(indexBean, csvParserFactory);

		CSVParser parser = Mockito.mock(CSVParser.class);

		Metadata metadata = new Metadata();
		metadata.setLocation("whoknows");
		metadata.setUpdated(10L);
		metadata.setLogger(logger);

		File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

		List<FieldLight> fields = new ArrayList<FieldLight>();
		no.difi.datahotel.model.Field field;
		field = new no.difi.datahotel.model.Field();
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
		
		indexBean.update(metadata);
		
		verify(parser, times(20000)).getNextLine();
		
		verify(logger).info("Building index.");
		verify(logger).info("Document 10000");
		verify(logger).info("Document 20000");
	}

	@Test
	public void testUnableToReadLine() throws Exception {
		FieldBean fieldEJB = Mockito.mock(FieldBean.class);
		Field settingsFieldField = IndexBean.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(indexBean, fieldEJB);

		Logger logger = Mockito.mock(Logger.class);

		CSVParserFactory csvParserFactory = Mockito.mock(CSVParserFactory.class);
		Field settingsFactoryField = IndexBean.class.getDeclaredField("csvParserFactory");
		settingsFactoryField.setAccessible(true);
		settingsFactoryField.set(indexBean, csvParserFactory);

		CSVParser parser = Mockito.mock(CSVParser.class);

		Metadata metadata = new Metadata();
		metadata.setLocation("whoknows2");
		metadata.setUpdated(10L);
		metadata.setLogger(logger);

		File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

		List<FieldLight> fields = new ArrayList<FieldLight>();
		no.difi.datahotel.model.Field field;
		field = new no.difi.datahotel.model.Field();
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
		
		indexBean.update(metadata);
		
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
		
		indexBean.update(metadata);
		searchBean.update(metadata);
		
		verify(logger).info("Building index.");
		
		indexBean.update(metadata);
		searchBean.update(metadata);
		
		verify(logger).info("Index up to date.");
	}
}
