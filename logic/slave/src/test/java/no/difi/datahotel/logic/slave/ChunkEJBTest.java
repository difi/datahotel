package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

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
		chunkEJB.update("difi", "test", "simple");

		assertTrue(Filesystem.getFileF("chunk", "difi", "test", "simple", "dataset-1.csv").exists());
		assertTrue(Filesystem.getFileF("chunk", "difi", "test", "simple", "dataset-2.csv").exists());
	}
	
	@Test
	public void testUpdateError() {
		chunkEJB.update("difi", "test", "simple-not-here");

		// TODO Fikse verifisert bruk av logger
		// Mockito.verify(logger).log(Level.WARNING, null);
	}

	@Test
	public void testGet() throws Exception {
		chunkEJB.update("difi", "test", "simple");
		
		assertEquals(100, chunkEJB.get("difi", "test", "simple", 1).size());
		assertEquals(19, chunkEJB.get("difi", "test", "simple", 2).size());
		
		assertNull(chunkEJB.get("difi", "test", "simple", 3));
		assertNull(chunkEJB.get("difi", "test", "simple2", 1));
		
		Thread.sleep(1000);
		
		chunkEJB.delete("difi", "test", "simple2");
	}
	
	@Test
	public void testOneHundred() throws Exception {
		chunkEJB.update("difi", "test", "hundred");		

		assertEquals(100, chunkEJB.get("difi", "test", "hundred", 1).size());
		assertNull(chunkEJB.get("difi", "test", "hundred", 2));
		
		Thread.sleep(1000);
		
		chunkEJB.delete("difi", "test", "hundred");
		assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test", "hundred").exists());
	}

	@Test
	public void testDelete() {
		chunkEJB.update("difi", "test", "simple");
		chunkEJB.delete("difi", "test", "simple");
		
		assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test").exists());
	}

}
