package no.difi.datahotel.logic.slave;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import no.difi.datahotel.util.bridge.Metadata;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateEJBTest {

	private static String realHome;
	
	private UpdateEJB updateEJB;

	private static FieldEJB fieldEJB;
	private static ChunkEJB chunkEJB;
	private static IndexEJB indexEJB;
	private static DataEJB dataEJB;
	private static Logger logger;
	private static Metadata metadata;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(UpdateEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		updateEJB = getUpdateEJB();

		logger = Mockito.mock(Logger.class);
		
		metadata = new Metadata();
		metadata.setLocation("difi/geo/fylke");
		metadata.setLogger(logger);
	}
	
	public static UpdateEJB getUpdateEJB() throws Exception {
		UpdateEJB c = new UpdateEJB();
		
		fieldEJB = Mockito.mock(FieldEJB.class);
		Field settingsFieldField = UpdateEJB.class.getDeclaredField("fieldEJB");
		settingsFieldField.setAccessible(true);
		settingsFieldField.set(c, fieldEJB);

		chunkEJB = Mockito.mock(ChunkEJB.class);
		Field settingsChunkField = UpdateEJB.class.getDeclaredField("chunkEJB");
		settingsChunkField.setAccessible(true);
		settingsChunkField.set(c, chunkEJB);

		indexEJB = Mockito.mock(IndexEJB.class);
		Field settingsIndexField = UpdateEJB.class.getDeclaredField("indexEJB");
		settingsIndexField.setAccessible(true);
		settingsIndexField.set(c, indexEJB);
		
		dataEJB = new DataEJB();
		Field settingsDataField = UpdateEJB.class.getDeclaredField("dataEJB");
		settingsDataField.setAccessible(true);
		settingsDataField.set(c, dataEJB);
		
		return c;
	}

	@Test
	public void testTriggerMissingTimestamp() {
		updateEJB.validate(metadata);
		
		verify(logger).warning("Missing timestamp in metadata file.");
	}
	
	@Test
	public void testTriggerUpdating() {
		dataEJB.setTimestamp(metadata.getLocation(), -1L);
		metadata.setUpdated(10L);

		updateEJB.validate(metadata);

		verify(logger).info("Do not disturb.");
	}

	@Test
	public void testNoAction() {
		dataEJB.setTimestamp(metadata.getLocation(), 10L);
		metadata.setUpdated(10L);

		updateEJB.validate(metadata);

		verifyZeroInteractions(logger);
	}
	
	@Test
	public void testNormalUpdateFirstTime() {
		metadata.setUpdated(10L);
		
		updateEJB.validate(metadata);

		verify(fieldEJB).update(metadata);
		verify(chunkEJB).update(metadata);
		verify(indexEJB).update(metadata);
		verify(logger).info("Ready");
	}

	@Test
	public void testNormalUpdateTimestampHigher() {
		dataEJB.setTimestamp(metadata.getLocation(), 5L);
		metadata.setUpdated(10L);
		
		updateEJB.validate(metadata);

		verify(fieldEJB).update(metadata);
		verify(chunkEJB).update(metadata);
		verify(indexEJB).update(metadata);
		verify(logger).info("Ready");
	}

	@Test
	public void testNormalUpdateTimestampLower() {
		dataEJB.setTimestamp(metadata.getLocation(), 15L);
		metadata.setUpdated(10L);
		
		updateEJB.validate(metadata);

		verify(fieldEJB).update(metadata);
		verify(chunkEJB).update(metadata);
		verify(indexEJB).update(metadata);
		verify(logger).info("Ready");
	}
}
