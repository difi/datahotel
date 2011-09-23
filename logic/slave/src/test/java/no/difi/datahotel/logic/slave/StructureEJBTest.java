package no.difi.datahotel.logic.slave;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;

import static no.difi.datahotel.util.shared.Filesystem.STRUCTURE;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SHARED;
import no.difi.datahotel.util.shared.Filesystem;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StructureEJBTest {

	private static String realHome;

	private StructureEJB structureEJB;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(StructureEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Before
	public void before() throws Exception {
		structureEJB = getStructureEJB();
	}
	
	public static StructureEJB getStructureEJB() throws Exception {
		FileUtils.copyFile(Filesystem.getFileF(FOLDER_SHARED, "datasetStructure-1.xml"), Filesystem.getFileF(FOLDER_SHARED, STRUCTURE));
		
		StructureEJB s = new StructureEJB();
		
		Field field;
		
		field = StructureEJB.class.getDeclaredField("chunkEJB");
		field.setAccessible(true);
		field.set(s, ChunkEJBTest.getChunkEJB());
		
		field = StructureEJB.class.getDeclaredField("indexEJB");
		field.setAccessible(true);
		field.set(s, IndexEJBTest.getIndexEJB());
		
		return s;
	}

	@Test
	public void update() throws Exception {
		
		File s = Filesystem.getFileF(FOLDER_SHARED, STRUCTURE);
		if (s.exists())
			s.delete();
		
		structureEJB.update();
		
		assertEquals(0, structureEJB.getOwners().size());
		
		FileUtils.copyFile(Filesystem.getFileF(FOLDER_SHARED, "datasetStructure-1.xml"), s);
		
		structureEJB.update();

		if (s.exists())
			s.delete();
		
		FileUtils.copyFile(Filesystem.getFileF(FOLDER_SHARED, "datasetStructure-2.xml"), s);

		s.setLastModified(new Date().getTime());
		
		structureEJB.update();
		
		assertEquals(0, structureEJB.getOwners().size());

		assertTrue(s.delete());
	}

	@Test
	public void testGets() throws Exception
	{
		assertEquals(0, structureEJB.getOwners().size());
		assertNull(structureEJB.getGroups("difi"));
		assertNull(structureEJB.getGroups("difi1"));
		assertNull(structureEJB.getDatasets("difi", "miljo"));
		assertNull(structureEJB.getDatasets("difi1", "miljo"));
		assertNull(structureEJB.getDatasets("difi", "miljo1"));
		assertNull(structureEJB.getDatasets("difi1", "miljo1"));
		
		structureEJB.update();
		
		assertEquals(1, structureEJB.getOwners().size());
		assertEquals(3, structureEJB.getGroups("difi").size());
		assertNull(structureEJB.getGroups("difi1"));
		assertEquals(1, structureEJB.getDatasets("difi", "miljo").size());

		assertNull(structureEJB.getDatasets("difi1", "miljo"));
		assertNull(structureEJB.getDatasets("difi", "miljo1"));
		assertNull(structureEJB.getDatasets("difi1", "miljo1"));

		
		assertTrue(structureEJB.getOwners().contains("difi"));
		assertTrue(structureEJB.getGroups("difi").contains("miljo"));
		assertTrue(structureEJB.getDatasets("difi", "miljo").contains("kalkulator"));

		structureEJB.update();
		
		assertEquals(1, structureEJB.getOwners().size());
		assertEquals(3, structureEJB.getGroups("difi").size());
		assertNull(structureEJB.getGroups("difi1"));
		assertEquals(1, structureEJB.getDatasets("difi", "miljo").size());

		assertTrue(structureEJB.getOwners().contains("difi"));
		assertTrue(structureEJB.getGroups("difi").contains("miljo"));
		assertTrue(structureEJB.getDatasets("difi", "miljo").contains("kalkulator"));

		Filesystem.getFileF(FOLDER_SHARED, STRUCTURE).setLastModified(new Date().getTime());

		structureEJB.update();
		
		assertEquals(1, structureEJB.getOwners().size());
		assertEquals(3, structureEJB.getGroups("difi").size());
		assertNull(structureEJB.getGroups("difi1"));
		assertEquals(1, structureEJB.getDatasets("difi", "miljo").size());

		assertTrue(structureEJB.getOwners().contains("difi"));
		assertTrue(structureEJB.getGroups("difi").contains("miljo"));
		assertTrue(structureEJB.getDatasets("difi", "miljo").contains("kalkulator"));
		
		assertTrue(Filesystem.getFileF(FOLDER_SHARED, STRUCTURE).delete());

	}
}
