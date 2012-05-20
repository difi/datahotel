package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.util.bridge.Metadata;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IndexEJBTest {

	private static String realHome;

	private String o = "difi", g = "miljo", d = "kalkulator";

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
	}

	@Test
	public void testIndex() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setLocation(o + "/" + g + "/" + d);
		metadata.setUpdated(System.currentTimeMillis());
		
		fieldEJB.update(metadata);
		indexEJB.update(o, g, d, 1);
	}
	
	@Test
	public void testNoSource()
	{
		indexEJB.update(o, g, "no-exists", 2);
	}
	
	@Test
	public void testNoIndex() {
		Exception ex = null;
		
		try
		{
			search.find("no", "dataset", "here", "kings", 1);
		} catch (Exception e)
		{
			ex = e;
		}
		
		assertNotNull(ex);
	}

	
	@Test
	public void testSearch() throws Exception {
		testIndex();
		
		assertEquals(2, search.find(o, g, d, "Energi", 1).size());
		assertEquals(0, search.find(o, g, d, "km", 1).size());
		assertEquals(1, search.find(o, g, d, "tog", 1).size());
		assertEquals(1, search.find(o, g, d, "ark", 1).size());
		assertEquals(2, search.find(o, g, d, "BUSS", 1).size());

		assertEquals(0, search.find(o, g, d, "Energi", 2).size());
		assertEquals(0, search.find(o, g, d, "km", 2).size());
		assertEquals(0, search.find(o, g, d, "tog", 2).size());
		assertEquals(0, search.find(o, g, d, "ark", 2).size());
		assertEquals(0, search.find(o, g, d, "BUSS", 2).size());
	}

	@Test
	public void testDelete() {
		indexEJB.delete("difi", "miljo", "kalkulator");
	}
	
	@Test
	public void testLookupAdv() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/geo/kommune");
		metadata.setUpdated(System.currentTimeMillis());
		
		fieldEJB.update(metadata);
		indexEJB.update("difi", "geo", "kommune", System.currentTimeMillis());
		
		Map<String, String> query = new HashMap<String, String>();
		query.put("kommune", "1401");
		query.put("fylke", "14");
		assertEquals(1, search.lookup("difi", "geo", "kommune", query, 1).size());
		assertEquals(0, search.lookup("difi", "geo", "kommune", query, 2).size());

		query.clear();
		query.put("kommune", "1401");
		assertEquals(1, search.lookup("difi", "geo", "kommune", query, 1).size());
		assertEquals(0, search.lookup("difi", "geo", "kommune", query, 2).size());
		
		query.clear();
		query.put("fylke", "14");
		assertEquals(26, search.lookup("difi", "geo", "kommune", query, 1).size());
		assertEquals(0, search.lookup("difi", "geo", "kommune", query, 2).size());

		query.clear();
		query.put("navn", "l*anger");
		assertEquals(2, search.lookup("difi", "geo", "kommune", query, 1).size());
		assertEquals(0, search.lookup("difi", "geo", "kommune", query, 2).size());

		indexEJB.delete("difi", "geo", "kommune");
	}
}
