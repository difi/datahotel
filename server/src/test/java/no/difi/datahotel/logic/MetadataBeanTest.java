package no.difi.datahotel.logic;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.logic.MetadataBean;
import no.difi.datahotel.logic.UpdateBean;
import no.difi.datahotel.util.Filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class MetadataBeanTest {

	private MetadataBean metadataBean;
	private DataBean dataBean;
	private Logger logger;
	
	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(MetadataBeanTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		metadataBean = getMetadataBean();
	}
	
	public MetadataBean getMetadataBean() throws Exception {

		dataBean = new DataBean();
		logger = Mockito.mock(Logger.class);
		
		MetadataBean m = new MetadataBean();
		m.setDataEJB(dataBean);
		m.setUpdateEJB(Mockito.mock(UpdateBean.class));

		Field settingsLoggerField = MetadataBean.class.getDeclaredField("logger");
		settingsLoggerField.setAccessible(true);
		settingsLoggerField.set(m, logger);
		
		return m;
	}

	@Test
	public void testReading() {
		metadataBean.update();
		assertEquals(1, dataBean.getChildren().size());
		assertEquals("http://www.difi.no/", dataBean.getChild("difi").getUrl());
		assertEquals(4, dataBean.getDatasets().size());
		
		assertEquals(null, dataBean.getChildren("not/seen/here"));
		
		assertEquals(2, dataBean.getChild("difi", "geo").getChildren().size());
	}
	
	@Test
	public void testError() throws IOException {
		File folder = Filesystem.getFolder(Filesystem.FOLDER_SLAVE, "google");
		File file = Filesystem.getFile(folder, "meta.xml");

		FileWriter fileWriter = new FileWriter(file);
		fileWriter.append("ERROR!");
		fileWriter.close();

		metadataBean.update();

		verify(logger).log(Level.WARNING, "Error while reading google");
		
		file.delete();
		folder.delete();
	}
}
