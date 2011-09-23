package no.difi.datahotel.logic.slave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.lang.reflect.Field;

import no.difi.datahotel.logic.slave.MetadataEJB.DatasetHolder;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetadataEJBTest {
	
	private MetadataEJB metadataEJB;

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(MetadataEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		metadataEJB = getMetadataEJB();
	}
	
	public MetadataEJB getMetadataEJB() throws Exception {
		MetadataEJB m = new MetadataEJB();

		StructureEJB s = StructureEJBTest.getStructureEJB();
		s.update();

		Field rField = MetadataEJB.class.getDeclaredField("structureEJB");
		rField.setAccessible(true);
		rField.set(m, s);	
		
		return m;
	}
	
	@Test
	public void testUpdate() {
		assertNull(metadataEJB.getOwners());
		assertNull(metadataEJB.getOwner("difi"));
		
		assertNull(metadataEJB.getGroups("difi"));
		assertNull(metadataEJB.getGroups("difi1"));
		assertNull(metadataEJB.getGroup("difi", "miljo"));
		
		assertNull(metadataEJB.getDatasets("difi", "miljo"));
		assertNull(metadataEJB.getDatasets("difi1", "miljo"));
		assertNull(metadataEJB.getDatasets("difi", "miljo1"));
		assertNull(metadataEJB.getDatasets("difi1", "miljo1"));
		assertNull(metadataEJB.getDataset("difi", "miljo", "kalkulator"));
		
		assertNull(metadataEJB.getDefinitions());
		assertNull(metadataEJB.getDefinition("annet"));
		assertNull(metadataEJB.getDefinition("annet1"));
		assertNull(metadataEJB.getDefinitionUsage("annet"));
		assertNull(metadataEJB.getDefinitionUsage("annet1"));
		
		assertNull(metadataEJB.getFields("difi", "miljo", "kalkulator"));
		assertNull(metadataEJB.getFields("difi1", "miljo", "kalkulator"));
		assertNull(metadataEJB.getFields("difi", "miljo1", "kalkulator"));		
		assertNull(metadataEJB.getFields("difi", "miljo", "kalkulator1"));		
		assertNull(metadataEJB.getFields("difi1", "miljo1", "kalkulator"));		
		assertNull(metadataEJB.getFields("difi1", "miljo", "kalkulator1"));		
		assertNull(metadataEJB.getFields("difi", "miljo1", "kalkulator1"));		
		assertNull(metadataEJB.getFields("difi1", "miljo1", "kalkulator1"));		

		metadataEJB.update();
		
		assertEquals(1, metadataEJB.getOwners().size());
		assertNotNull(metadataEJB.getOwner("difi"));
		assertNull(metadataEJB.getOwner("difi1"));
		
		assertEquals(3, metadataEJB.getGroups("difi").size());
		assertNull(metadataEJB.getGroups("difi1"));
		assertNotNull(metadataEJB.getGroup("difi", "miljo"));
		assertNull(metadataEJB.getGroup("difi1", "miljo"));
		assertNull(metadataEJB.getGroup("difi", "miljo1"));
		
		assertEquals(1, metadataEJB.getDatasets("difi", "miljo").size());
		assertNull(metadataEJB.getDatasets("difi1", "miljo"));
		assertNull(metadataEJB.getDatasets("difi", "miljo1"));
		assertNull(metadataEJB.getDatasets("difi1", "miljo1"));
		assertNotNull(metadataEJB.getDataset("difi", "miljo", "kalkulator"));
		assertNull(metadataEJB.getDataset("difi1", "miljo", "kalkulator"));
		assertNull(metadataEJB.getDataset("difi", "miljo1", "kalkulator"));
		assertNull(metadataEJB.getDataset("difi", "miljo", "kalkulator1"));
		
		assertEquals(6, metadataEJB.getDefinitions().size());
		assertEquals("navn", metadataEJB.getDefinition("navn").getShortName());
		assertNull(metadataEJB.getDefinition("annet"));
		assertEquals(4, metadataEJB.getDefinitionUsage("navn").size());
		assertNull(metadataEJB.getDefinitionUsage("annet"));
		
		assertEquals(4, metadataEJB.getLast(5).size());
		
		assertEquals(4, metadataEJB.getFields("difi", "miljo", "kalkulator").getFields().size());
		assertNull(metadataEJB.getFields("difi1", "miljo", "kalkulator"));
		assertNull(metadataEJB.getFields("difi", "miljo1", "kalkulator"));		
		assertNull(metadataEJB.getFields("difi", "miljo", "kalkulator1"));		
		assertNull(metadataEJB.getFields("difi1", "miljo1", "kalkulator"));		
		assertNull(metadataEJB.getFields("difi1", "miljo", "kalkulator1"));		
		assertNull(metadataEJB.getFields("difi", "miljo1", "kalkulator1"));		
		assertNull(metadataEJB.getFields("difi1", "miljo1", "kalkulator1"));		
	}
	
	@Test
	public void testNoOwners() throws Exception {
		Field rField = MetadataEJB.class.getDeclaredField("structureEJB");
		rField.setAccessible(true);
		rField.set(metadataEJB, new StructureEJB());	
		
		assertNull(metadataEJB.getOwners());
		
		metadataEJB.update();

		assertEquals(0, metadataEJB.getOwners().size());
	}
	
	@Test
	public void testDatasetHolder() {
		assertNull(metadataEJB.getLast(5));
		
		metadataEJB.update();
		
		assertEquals(4, metadataEJB.getLast(5).size());
		assertEquals(1, metadataEJB.getLast(1).size());
		assertEquals(0, metadataEJB.getLast(0).size());
		
		DatasetHolder dh = metadataEJB.getLast(5).get(3);
		assertEquals(metadataEJB.getDataset("difi", "miljo", "kalkulator"), dh.getDataset());
		assertNotNull(dh.getTimestamp());
		
		// DatasetHolder dhOld = DatasetHolder(0L, new Dataset());
		
		assertEquals(0, dh.compareTo(dh));
		// assertEquals(0, dh.compareTo(dhOld));
	}
}
