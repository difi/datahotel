package no.difi.datahotel.util.bridge;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StructureTest {

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
	public void testSaveRead() throws Exception {
		Structure s = new Structure();
		
		Map<String, Map<String, Map<String, Long>>> data = new HashMap<String, Map<String, Map<String, Long>>>();
		s.setStructure(data);
		
		Map<String, Map<String, Long>> owner = new HashMap<String, Map<String, Long>>();
		data.put("difi", owner);
		
		Map<String, Long> group = new HashMap<String, Long>();
		owner.put("miljo", group);
		
		group.put("kalkulator", Long.MAX_VALUE);
		
		s.save();
		
		Structure s2 = Structure.read();
		
		Assert.assertEquals(s, s2);
		Assert.assertEquals(s.getStructure(), s2.getStructure());
	}
	
	@Test
	public void testEquals() {
		Structure d1 = new Structure();
		Structure d2 = new Structure();

		assertTrue(d1.equals(d1));
		assertFalse(d1.equals(null));
		assertFalse(d1.equals("Hello world!"));

		assertTrue(d1.equals(d2));
		assertTrue(d2.equals(d1));

		Map<String, Map<String,Map<String,Long>>> s = new HashMap<String, Map<String,Map<String,Long>>>();
		s.put("difi", new HashMap<String, Map<String,Long>>());
		d1.setStructure(s);
		assertFalse(d1.equals(d2));
		assertFalse(d2.equals(d1));
		
		d2.setStructure(null);
		assertFalse(d1.equals(d2));
		assertFalse(d2.equals(d1));
		
		d1.setStructure(null);
		assertTrue(d1.equals(d2));
		assertTrue(d2.equals(d1));
	}
}
