package no.difi.datahotel.slave.logic;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.slave.logic.DataEJB;
import no.difi.datahotel.util.model.Metadata;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataEJBTest {

	private static String realHome;

	private DataEJB dataEJB;
	private Map<String, Metadata> directory;
	private Metadata root;
	
	private Metadata m1;
	private Metadata m2;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(DataEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Before
	public void before() throws Exception {
		root = new Metadata();

		directory = new HashMap<String, Metadata>();
		directory.put("", root);

		m1 = new Metadata();
		m1.setLocation("difi");
		m1.setActive(true);

		m2 = new Metadata();
		m2.setLocation("fad");
		m2.setActive(true);
		
		directory.put("difi", m1);
		directory.put("fad", m2);
		
		root.addChild(m1);
		root.addChild(m2);
		
		dataEJB = new DataEJB();
		dataEJB.setDirectory(directory);
	}
	
	@Test
	public void testSimple() {
		assertEquals(m2, dataEJB.getChild("fad"));
		assertEquals(2, dataEJB.getChildren().size());
	}

	@Test
	public void testSomeInactive() {
		m2.setActive(false);
		
		assertEquals(m2, dataEJB.getChild("fad"));
		assertEquals(1, dataEJB.getChildren().size());
	}

	@Test
	public void testAllInactive() {
		m1.setActive(false);
		m2.setActive(false);
		
		assertEquals(m2, dataEJB.getChild("fad"));
		assertEquals(null, dataEJB.getChildren());
	}

	@Test
	public void testChildNotFound() {
		assertEquals(null, dataEJB.getChild("smk"));
		assertEquals(null, dataEJB.getChildren("smk"));
	}
	
	@Test
	public void testDataset() {
		// No datasets
		assertEquals(0, dataEJB.getDatasets().size());
		
		// One active dataset
		m1.setDataset(true);
		assertEquals(1, dataEJB.getDatasets().size());

		// One inactive dataset
		m1.setActive(false);
		assertEquals(0, dataEJB.getDatasets().size());
		
		// One inactive and one active
		m2.setDataset(true);
		assertEquals(1, dataEJB.getDatasets().size());

		// Two active dataset
		m1.setActive(true);
		assertEquals(2, dataEJB.getDatasets().size());
	}

}
