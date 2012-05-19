package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetadataEJBTest {

	private MetadataEJB metadata2EJB;
	
	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(MetadataEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		metadata2EJB = getMetadata2EJB();
	}
	
	public MetadataEJB getMetadata2EJB() throws Exception {
		MetadataEJB m = new MetadataEJB();
		
		return m;
	}

	@Test
	public void testRecursion() {
		metadata2EJB.update();
		assertEquals(1, metadata2EJB.getChildren().size());
		assertEquals("http://www.difi.no/", metadata2EJB.getChild("difi").getUrl());
	}
}
