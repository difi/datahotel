package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_CACHE_CHUNK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
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
	static Metadata metadata;
	
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
		
		metadata = new Metadata();
		metadata.setUpdated(System.currentTimeMillis());
		metadata.setLogger(logger);
		
		return c;
	}
	
	@Test
	public void testUpdate() {
		metadata.setLocation("difi/test/simple");
		metadata.setShortName("simple");

		chunkEJB.update(metadata);

		assertTrue(Filesystem.getFile(FOLDER_CACHE_CHUNK, "difi", "test", "simple", "dataset-1.csv").exists());
		assertTrue(Filesystem.getFile(FOLDER_CACHE_CHUNK, "difi", "test", "simple", "dataset-2.csv").exists());
	}
	
	@Test
	public void testUpdateError() {
		metadata.setLocation("difi/test/simple-not-here");
		metadata.setShortName("simple-not-here");

		chunkEJB.update(metadata);

		// TODO Fikse verifisert bruk av logger
		// Mockito.verify(logger).log(Level.WARNING, null);
	}

	@Test
	public void testGet() throws Exception {
		metadata.setLocation("difi/test/simple");
		metadata.setShortName("simple");

		chunkEJB.update(metadata);
		
		assertEquals(100, chunkEJB.get(metadata, 1).size());
		assertEquals(19, chunkEJB.get(metadata, 2).size());
		
		assertNull(chunkEJB.get(metadata, 3));
		
		metadata.setLocation("difi/test/simple2");
		assertNull(chunkEJB.get(metadata, 1));
		
		// Thread.sleep(1000);
		
		// chunkEJB.delete("difi", "test", "simple2");
	}
	
	@Test
	public void testOneHundred() throws Exception {
		metadata.setLocation("difi/test/hundred");
		metadata.setShortName("hundred");

		chunkEJB.update(metadata);		

		assertEquals(100, chunkEJB.get(metadata, 1).size());
		assertNull(chunkEJB.get(metadata, 2));
		
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
		metadata.setLocation("difi/test/hundred");
		metadata.setShortName("hundred");

		chunkEJB.update(metadata);		

		assertEquals(100, chunkEJB.get(metadata, 1).size());
		assertNull(chunkEJB.get(metadata, 2));
		long ts = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "timestamp").lastModified();
		
		Thread.sleep(1000);
		
		chunkEJB.update(metadata);
		long ts2 = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "timestamp").lastModified();
		assertEquals(ts, ts2);
		
		// chunkEJB.delete("difi", "test", "hundred");
		// assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test", "hundred").exists());
	}

	@Test
	public void testReplaceGoal() throws Exception {
		File folder = Filesystem.getFolder(Filesystem.FOLDER_CACHE_CHUNK, "difi/test/hundred", "secret");
		assertTrue(folder.exists());
		
		metadata.setLocation("difi/test/hundred");
		metadata.setShortName("hundred");

		chunkEJB.update(metadata);
		
		assertFalse(folder.exists());
	}
}
