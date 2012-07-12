package no.difi.datahotel.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.DatahotelException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataBeanTest {

	private static String realHome;

	private DataBean dataBean;
	private Map<String, Metadata> directory;
	private Metadata root;
	
	private Metadata m1;
	private Metadata m2;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(DataBeanTest.class.getResource("/").toURI()).toString());
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
		
		dataBean = new DataBean();
		dataBean.setDirectory(directory);
	}
	
	@Test
	public void testSimple() {
		assertEquals(m2, dataBean.getChild("fad"));
		assertEquals(2, dataBean.getChildren().size());
	}

	@Test
	public void testSomeInactive() {
		m2.setActive(false);
		
		assertEquals(m2, dataBean.getChild("fad"));
		assertEquals(1, dataBean.getChildren().size());
	}

	@Test
	public void testAllInactive() {
		m1.setActive(false);
		m2.setActive(false);
		
		assertEquals(m2, dataBean.getChild("fad"));
		assertEquals(null, dataBean.getChildren());
	}

	@Test
	public void testChildNotFound() {
		DatahotelException exception;
		
		exception = null;
		try {
			dataBean.getChild("smk");			
		} catch (DatahotelException e) {
			exception = e;
		}
		assertNotNull(exception);


		assertEquals(null, dataBean.getChildren("smk"));			
	}
	
	@Test
	public void testDataset() {
		// No datasets
		assertEquals(0, dataBean.getDatasets().size());
		
		// One active dataset
		m1.setDataset(true);
		assertEquals(1, dataBean.getDatasets().size());

		// One inactive dataset
		m1.setActive(false);
		assertEquals(0, dataBean.getDatasets().size());
		
		// One inactive and one active
		m2.setDataset(true);
		assertEquals(1, dataBean.getDatasets().size());

		// Two active dataset
		m1.setActive(true);
		assertEquals(2, dataBean.getDatasets().size());
	}

}
