package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;

import java.io.File;

import no.difi.datahotel.util.bridge.Metadata;

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
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/geo/fylke");
		metadata.setUpdated(System.currentTimeMillis());
		
		// Update first
		fieldEJB.update(metadata);
		assertEquals(2, fieldEJB.getFields(metadata).size());

		assertEquals(3, fieldEJB.getDefinitions().size());
		assertEquals("Navn", fieldEJB.getDefinition("navn").getName());
		
		assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldEJB.getUsage("kommuner"));

		// Update second
		fieldEJB.update(metadata);
		assertEquals(2, fieldEJB.getFields(metadata).size());

		assertEquals(3, fieldEJB.getDefinitions().size());
		assertEquals("Navn", fieldEJB.getDefinition("navn").getName());
		
		assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldEJB.getUsage("kommuner"));

	}
}
