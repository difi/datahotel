package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetadataEJBTest {

	private MetadataEJB metadataEJB;
	private DataEJB dataEJB;
	
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
		metadataEJB = getMetadata2EJB();
	}
	
	public MetadataEJB getMetadata2EJB() throws Exception {
		MetadataEJB m = new MetadataEJB();
		
		dataEJB = DataEJBTest.getDataEJB();
		Field settingsDataField = MetadataEJB.class.getDeclaredField("dataEJB");
		settingsDataField.setAccessible(true);
		settingsDataField.set(m, dataEJB);

		UpdateEJB updateEJB = UpdateEJBTest.getUpdateEJB();
		Field settingsUpdateField = MetadataEJB.class.getDeclaredField("updateEJB");
		settingsUpdateField.setAccessible(true);
		settingsUpdateField.set(m, updateEJB);
		
		return m;
	}

	@Test
	public void testReading() {
		metadataEJB.update();
		assertEquals(1, dataEJB.getChildren().size());
		assertEquals("http://www.difi.no/", dataEJB.getChild("difi").getUrl());
		assertEquals(4, dataEJB.getDatasets().size());
		
		assertEquals(null, dataEJB.getChildren("not/seen/here"));
		
		assertEquals(2, dataEJB.getChild("difi", "geo").getChildren().size());
	}
}
