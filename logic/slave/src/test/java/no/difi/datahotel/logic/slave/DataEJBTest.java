package no.difi.datahotel.logic.slave;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class DataEJBTest {

	private static String realHome;
	
	private DataEJB dataEJB;
	
	static Logger logger;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(ChunkEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		dataEJB = getDataEJB();
	}
	
	public static DataEJB getDataEJB() throws Exception {
		DataEJB c = new DataEJB();
		
		/*logger = Mockito.mock(Logger.class);
		Field rField = ChunkEJB.class.getDeclaredField("logger");
		rField.setAccessible(true);
		rField.set(c, logger); */

		return c;
	}

}
