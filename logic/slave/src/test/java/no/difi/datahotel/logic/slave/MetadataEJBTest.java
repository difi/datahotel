package no.difi.datahotel.logic.slave;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class MetadataEJBTest {

	private MetadataEJB metadataEJB;
	private DataEJB dataEJB;
	private Logger logger;
	
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

		dataEJB = new DataEJB();
		Field settingsDataField = MetadataEJB.class.getDeclaredField("dataEJB");
		settingsDataField.setAccessible(true);
		settingsDataField.set(m, dataEJB);

		logger = Mockito.mock(Logger.class);
		Field settingsLoggerField = MetadataEJB.class.getDeclaredField("logger");
		settingsLoggerField.setAccessible(true);
		settingsLoggerField.set(m, logger);

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
	
	@Test
	public void testError() throws IOException {
		File folder = Filesystem.getFolder(Filesystem.FOLDER_SLAVE, "google");
		File file = Filesystem.getFile(folder, "meta.xml");

		FileWriter fileWriter = new FileWriter(file);
		fileWriter.append("ERROR!");
		fileWriter.close();

		metadataEJB.update();

		verify(logger).log(Level.WARNING, "Error while reading google");
		
		file.delete();
		folder.delete();
	}
}
