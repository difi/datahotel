package no.difi.datahotel.logic.slave;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Metadata2EJBTest {

	private Metadata2EJB metadata2EJB;
	
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
	
	public Metadata2EJB getMetadata2EJB() throws Exception {
		Metadata2EJB m = new Metadata2EJB();

		Field rField = Metadata2EJB.class.getDeclaredField("systemEJB");
		rField.setAccessible(true);
		rField.set(m, new SystemEJB());	
		
		return m;
	}

	@Test
	public void testRecursion() {
		metadata2EJB.update();
	}
}
