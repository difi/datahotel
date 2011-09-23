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

public class OwnerTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(OwnerTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Test
	public void testSetGet() throws Exception {
		Owner o = new Owner();

		assertNull(o.getName());
		assertNull(o.getShortName());
		assertNull(o.getUrl());

		o.setName("Direktoratet for forvaltning og IKT");
		o.setShortName("difi");
		o.setUrl("http://www.difi.no/");

		assertEquals("Direktoratet for forvaltning og IKT", o.getName());
		assertEquals("difi", o.getShortName());
		assertEquals("http://www.difi.no/", o.getUrl());
	}

	@Test
	public void testEquals() throws Exception {
		Owner o1 = new Owner();
		o1.setShortName("difi");

		assertFalse(o1.equals(null));
		assertFalse(o1.equals("Hello world!"));
		assertTrue(o1.equals(o1));

		Owner o2 = new Owner();
		o2.setShortName("difi");

		assertTrue(o1.equals(o2));

		o2.setShortName("fad");

		assertFalse(o1.equals(o2));
	}

	@Test
	public void testSaveRead() throws Exception {
		Owner o = new Owner();
		o.setName("Direktoratet for forvaltning og IKT");
		o.setShortName("difi");
		o.setUrl("http://www.difi.no/");

		o.save();
		assertTrue(Filesystem.getFileF(Filesystem.FOLDER_SHARED, o.getShortName(), Filesystem.OWNER_METADATA).exists());
		
		Owner o2 = Owner.read(o.getShortName());
		assertTrue(o.equals(o2));
	}
}
