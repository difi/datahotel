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

public class DatasetTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(DatasetTest.class.getResource("/").toURI()).toString());
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
		g.setName("Miljodata");
		g.setShortName("miljo");
		g.setUrl("http://www.difi.no/");
		g.setDescription("Data fra ANS");
		g.setOwner(o);

		Dataset d = new Dataset();

		assertNull(d.getName());
		assertNull(d.getShortName());
		assertNull(d.getDescription());
		assertNull(d.getOwner());
		assertNull(d.getGroup());

		d.setName("Kalkulator");
		d.setShortName("kalkulator");
		d.setDescription("Grunndata");
		d.setOwner(o);
		d.setGroup(g);

		assertEquals("Kalkulator", d.getName());
		assertEquals("kalkulator", d.getShortName());
		assertEquals("Grunndata", d.getDescription());
		assertEquals("difi", d.getOwner());
		assertEquals("miljo", d.getGroup());
	}

	@Test
	public void testEquals() throws Exception {
		Dataset d1 = new Dataset();
		Dataset d2 = new Dataset();

		assertTrue(d1.equals(d1));
		assertFalse(d1.equals(null));
		assertFalse(d1.equals("Hello world!"));
		assertTrue(d1.equals(d2));

		d1.setShortName("kalkulator");
		d2.setShortName("grunndata");
		assertFalse(d1.equals(d2));

		d2.setShortName("kalkulator");
		assertTrue(d1.equals(d2));

		d1.setOwner("difi");
		assertFalse(d1.equals(d2));

		d2.setOwner("difi");
		assertTrue(d1.equals(d2));

		d1.setGroup("miljo");
		assertFalse(d1.equals(d2));

		d2.setGroup("miljo");
		assertTrue(d1.equals(d2));
	}

	@Test
	public void testCompareTo() {
		Dataset d1 = new Dataset();
		Dataset d2 = new Dataset();

		d1.setName("Kalkulator");
		d2.setName("Forbruk");

		assertTrue(d1.compareTo(d2) > 0);
		assertTrue(d2.compareTo(d1) < 0);
		assertTrue(d2.compareTo(d2) == 0);
	}

	@Test
	public void testSaveRead() throws Exception {
		Dataset d = new Dataset();
		d.setName("Kalkulator");
		d.setShortName("kalkulator");
		d.setDescription("Grunndata");
		d.setOwner("difi");
		d.setGroup("miljo");

		d.save();
		assertTrue(Filesystem.getFileF(Filesystem.FOLDER_SHARED, d.getOwner(), d.getGroup(), d.getShortName(),
				Filesystem.DATASET_METADATA).exists());

		Dataset d2 = Dataset.read(d.getOwner(), d.getGroup(), d.getShortName());
		assertTrue(d.equals(d2));
	}
}
