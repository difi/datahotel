package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_CHUNK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ChunkEJBTest {

	private static String realHome;
	
	private ChunkEJB chunkEJB;
	
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
		chunkEJB = getChunkEJB();
	}
	
	public static ChunkEJB getChunkEJB() throws Exception {
		ChunkEJB c = new ChunkEJB();
		
		logger = Mockito.mock(Logger.class);
		
		Field rField = ChunkEJB.class.getDeclaredField("logger");
		rField.setAccessible(true);
		rField.set(c, logger);
		
		return c;
	}
	
	@Test
	public void testUpdate() {
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/test/simple");
		metadata.setShortName("simple");
		metadata.setUpdated(System.currentTimeMillis());

		chunkEJB.update(metadata);

		assertTrue(Filesystem.getFileF("chunk", "difi", "test", "simple", "dataset-1.csv").exists());
		assertTrue(Filesystem.getFileF("chunk", "difi", "test", "simple", "dataset-2.csv").exists());
	}
	
	@Test
	public void testUpdateError() {
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/test/simple-not-here");
		metadata.setShortName("simple-not-here");
		metadata.setUpdated(System.currentTimeMillis());

		chunkEJB.update(metadata);

		// TODO Fikse verifisert bruk av logger
		// Mockito.verify(logger).log(Level.WARNING, null);
	}

	@Test
	public void testGet() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/test/simple");
		metadata.setShortName("simple");
		metadata.setUpdated(System.currentTimeMillis());

		chunkEJB.update(metadata);
		
		assertEquals(100, chunkEJB.get(metadata.getLocation(), 1).size());
		assertEquals(19, chunkEJB.get(metadata.getLocation(), 2).size());
		
		assertNull(chunkEJB.get(metadata.getLocation(), 3));
		assertNull(chunkEJB.get("difi/test/simple2", 1));
		
		// Thread.sleep(1000);
		
		// chunkEJB.delete("difi", "test", "simple2");
	}
	
	@Test
	public void testOneHundred() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/test/hundred");
		metadata.setShortName("hundred");
		metadata.setUpdated(System.currentTimeMillis());

		chunkEJB.update(metadata);		

		assertEquals(100, chunkEJB.get(metadata.getLocation(), 1).size());
		assertNull(chunkEJB.get(metadata.getLocation(), 2));
		
		assertEquals(new Long(1), chunkEJB.getPages("difi/test/hundred"));
		assertEquals(new Long(0), chunkEJB.getPages("difi/test/hundred200"));
		
		assertEquals(new Long(100), chunkEJB.getPosts("difi/test/hundred"));
		assertEquals(new Long(0), chunkEJB.getPosts("difi/test/hundred200"));
		
		Thread.sleep(1000);
		
		// chunkEJB.delete("difi", "test", "hundred");
		// assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test", "hundred").exists());
	}

	@Test
	public void testNoNeed() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/test/hundred");
		metadata.setShortName("hundred");
		metadata.setUpdated(System.currentTimeMillis());

		chunkEJB.update(metadata);		

		assertEquals(100, chunkEJB.get(metadata.getLocation(), 1).size());
		assertNull(chunkEJB.get(metadata.getLocation(), 2));
		long ts = Filesystem.getFileF(FOLDER_CHUNK, metadata.getLocation(), "timestamp").lastModified();
		
		Thread.sleep(1000);
		
		chunkEJB.update(metadata);
		long ts2 = Filesystem.getFileF(FOLDER_CHUNK, metadata.getLocation(), "timestamp").lastModified();
		assertEquals(ts, ts2);
		
		// chunkEJB.delete("difi", "test", "hundred");
		// assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test", "hundred").exists());
	}
}
