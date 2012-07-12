package no.difi.datahotel.logic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import no.difi.datahotel.logic.ChunkBean;
import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.logic.FieldBean;
import no.difi.datahotel.logic.IndexBean;
import no.difi.datahotel.logic.UpdateBean;
import no.difi.datahotel.model.Metadata;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateBeanTest {

	private static String realHome;
	
	private UpdateBean updateEJB;

	private static FieldBean fieldBean;
	private static ChunkBean chunkBean;
	private static IndexBean indexBean;
	private static DataBean dataBean;
	private static Logger logger;
	private static Metadata metadata;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(UpdateBeanTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		updateEJB = getUpdateBean();

		logger = Mockito.mock(Logger.class);
		
		metadata = new Metadata();
		metadata.setLocation("difi/geo/fylke");
		metadata.setLogger(logger);
	}
	
	public static UpdateBean getUpdateBean() throws Exception {
		UpdateBean c = new UpdateBean();
		
		fieldBean = Mockito.mock(FieldBean.class);
		Field settingsFieldField = UpdateBean.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(c, fieldBean);

		chunkBean = Mockito.mock(ChunkBean.class);
		Field settingsChunkField = UpdateBean.class.getDeclaredField("chunkEJB");
		settingsChunkField.setAccessible(true);
		settingsChunkField.set(c, chunkBean);

		indexBean = Mockito.mock(IndexBean.class);
		Field settingsIndexField = UpdateBean.class.getDeclaredField("indexEJB");
		settingsIndexField.setAccessible(true);
		settingsIndexField.set(c, indexBean);
		
		dataBean = new DataBean();
		Field settingsDataField = UpdateBean.class.getDeclaredField("dataEJB");
		settingsDataField.setAccessible(true);
		settingsDataField.set(c, dataBean);
		
		return c;
	}

	@Test
	public void testTriggerMissingTimestamp() {
		updateEJB.validate(metadata);
		
		verify(logger).warning("Missing timestamp in metadata file.");
	}
	
	@Test
	public void testTriggerUpdating() {
		dataBean.setTimestamp(metadata.getLocation(), -1L);
		metadata.setUpdated(10L);

		updateEJB.validate(metadata);

		verify(logger).info("Do not disturb.");
	}

	@Test
	public void testNoAction() {
		dataBean.setTimestamp(metadata.getLocation(), 10L);
		metadata.setUpdated(10L);

		updateEJB.validate(metadata);

		verifyZeroInteractions(logger);
	}
	
	@Test
	public void testNormalUpdateFirstTime() {
		metadata.setUpdated(10L);
		
		updateEJB.validate(metadata);

		verify(fieldBean).update(metadata);
		verify(chunkBean).update(metadata);
		verify(indexBean).update(metadata);
		verify(logger).info("Ready");
	}

	@Test
	public void testNormalUpdateTimestampHigher() {
		dataBean.setTimestamp(metadata.getLocation(), 5L);
		metadata.setUpdated(10L);
		
		updateEJB.validate(metadata);

		verify(fieldBean).update(metadata);
		verify(chunkBean).update(metadata);
		verify(indexBean).update(metadata);
		verify(logger).info("Ready");
	}

	@Test
	public void testNormalUpdateTimestampLower() {
		dataBean.setTimestamp(metadata.getLocation(), 15L);
		metadata.setUpdated(10L);
		
		updateEJB.validate(metadata);

		verify(fieldBean).update(metadata);
		verify(chunkBean).update(metadata);
		verify(indexBean).update(metadata);
		verify(logger).info("Ready");
	}
}
