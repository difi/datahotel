package no.difi.datahotel.util.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GroupTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(GroupTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Test
	public void testSetGet() throws Exception {
		Owner o = new Owner();
		o.setName("Direktoratet for forvaltning og IKT");
		o.setShortName("difi");
		o.setUrl("http://www.difi.no/");
		
		Group g = new Group();

		assertNull(g.getName());
		assertNull(g.getShortName());
		assertNull(g.getUrl());
		assertNull(g.getDescription());
		assertNull(g.getOwner());

		g.setName("Miljodata");
		g.setShortName("miljo");
		g.setUrl("http://www.difi.no/");
		g.setDescription("Data fra ANS");
		g.setOwner(o);

		assertEquals("Miljodata", g.getName());
		assertEquals("miljo", g.getShortName());
		assertEquals("http://www.difi.no/", g.getUrl());
		assertEquals("Data fra ANS", g.getDescription());
		assertEquals("difi", g.getOwner());
	}

	@Test
	public void testEquals() throws Exception {
		Group g1 = new Group();
		Group g2 = new Group();

		assertTrue(g1.equals(g1));
		assertFalse(g1.equals(null));
		assertFalse(g1.equals("Hello world!"));
		assertTrue(g1.equals(g2));

		g1.setShortName("miljo");
		g2.setShortName("norge");
		assertFalse(g1.equals(g2));

		g2.setShortName("miljo");
		assertTrue(g1.equals(g2));
		
		g1.setOwner("difi");
		assertFalse(g1.equals(g2));

		g2.setOwner("difi");
		assertTrue(g1.equals(g2));
	}

	@Test
	public void testSaveRead() throws Exception {
		Owner o = new Owner();
		o.setName("Direktoratet for forvaltning og IKT");
		o.setShortName("difi");
		o.setUrl("http://www.difi.no/");
		
		Group g = new Group();
		g.setName("Miljodata");
		g.setShortName("miljo");
		g.setUrl("http://www.difi.no/");
		g.setDescription("Data fra ANS");
		g.setOwner(o);

		g.save();
		assertTrue(Filesystem.getFileF(Filesystem.FOLDER_SHARED, o.getShortName(), g.getShortName(), Filesystem.GROUP_METADATA).exists());
		
		Group g2 = Group.read(o.getShortName(), g.getShortName());
		assertTrue(g.equals(g2));
	}
}
