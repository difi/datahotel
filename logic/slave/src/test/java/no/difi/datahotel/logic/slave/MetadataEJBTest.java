package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Field;

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
		
		FieldEJB fieldEJB = new FieldEJB();
		Field settingsFieldField = MetadataEJB.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(m, fieldEJB);

		ChunkEJB chunkEJB = new ChunkEJB();
		Field settingsChunkField = MetadataEJB.class.getDeclaredField("chunkEJB");
		settingsChunkField.setAccessible(true);
		settingsChunkField.set(m, chunkEJB);

		IndexEJB indexEJB = new IndexEJB();
		Field settingsIndexField = MetadataEJB.class.getDeclaredField("indexEJB");
		settingsIndexField.setAccessible(true);
		settingsIndexField.set(m, indexEJB);
		
		return m;
	}

	@Test
	public void testReading() {
		metadata2EJB.update();
		assertEquals(1, metadata2EJB.getChildren().size());
		assertEquals("http://www.difi.no/", metadata2EJB.getChild("difi").getUrl());
		assertEquals(4, metadata2EJB.getDatasets().size());
		assertEquals(null, metadata2EJB.getChildren("not/seen/here"));
	}
}
