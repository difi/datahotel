package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IndexEJBTest {

	private static String realHome;

	private String o = "difi", g = "miljo", d = "kalkulator";

	private IndexEJB indexEJB;
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
		indexEJB = getIndexEJB();
		search = new SearchEJB();
	}

	public static IndexEJB getIndexEJB() {
		// TODO Ta over logger i IndexEJB.
		
		return new IndexEJB();
	}
	
	@Test
	public void testIndex() throws Exception {
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
			search.find("no", "dataset", "here", "kings");
		} catch (Exception e)
		{
			ex = e;
		}
		
		assertNotNull(ex);
	}

	
	@Test
	public void testSearch() throws Exception {
		testIndex();
		
		assertEquals(2, search.find(o, g, d, "Energi").size());
		assertEquals(0, search.find(o, g, d, "km").size());
		assertEquals(1, search.find(o, g, d, "tog").size());
		assertEquals(1, search.find(o, g, d, "ark").size());
		assertEquals(2, search.find(o, g, d, "BUSS").size());
	}

	@Test
	public void testDelete() {
		indexEJB.delete("difi", "miljo", "kalkulator");
	}
	
	@Test
	public void testLookupAdv() throws Exception {
		indexEJB.update("difi", "geo", "kommune", 3);
		
		Map<String, String> query = new HashMap<String, String>();
		query.put("kommune", "1401");
		query.put("fylke", "14");
		assertEquals(1, search.lookup("difi", "geo", "kommune", query).size());

		query.clear();
		query.put("kommune", "1401");
		assertEquals(1, search.lookup("difi", "geo", "kommune", query).size());
		
		query.clear();
		query.put("fylke", "14");
		assertEquals(26, search.lookup("difi", "geo", "kommune", query).size());

		query.clear();
		query.put("navn", "l*anger");
		assertEquals(2, search.lookup("difi", "geo", "kommune", query).size());

		indexEJB.delete("difi", "geo", "kommune");
	}
}
