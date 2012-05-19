package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;

import java.io.File;

import no.difi.datahotel.util.bridge.MetadataSlave;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FieldEJBTest {

	private FieldEJB fieldEJB;
	
	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(FieldEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		fieldEJB = getFieldEJB();
	}
	
	public FieldEJB getFieldEJB() throws Exception {
		return new FieldEJB();
	}

	@Test
	public void testSimple() {
		MetadataSlave m = new MetadataSlave();
		m.setLocation("difi/geo/fylke");
		m.setUpdated(System.currentTimeMillis());
		
		// Update first
		fieldEJB.update("difi", "geo", "fylke");
		assertEquals(2, fieldEJB.getFields("difi", "geo", "fylke").size());

		assertEquals(2, fieldEJB.getDefinitions().size());
		assertEquals("Navn", fieldEJB.getDefinition("navn").getName());
		
		assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldEJB.getUsage("kommuner"));

		// Update second
		fieldEJB.update("difi", "geo", "fylke");
		assertEquals(2, fieldEJB.getFields("difi", "geo", "fylke").size());

		assertEquals(2, fieldEJB.getDefinitions().size());
		assertEquals("Navn", fieldEJB.getDefinition("navn").getName());
		
		assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldEJB.getUsage("kommuner"));

	}
}
