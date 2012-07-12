package no.difi.datahotel.logic;

import static org.junit.Assert.assertEquals;

import java.io.File;

import no.difi.datahotel.logic.MasterDefinitionBean;
import no.difi.datahotel.logic.FieldBean;
import no.difi.datahotel.model.Definition;
import no.difi.datahotel.model.Metadata;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FieldBeanTest {

	private FieldBean fieldBean;
	private MasterDefinitionBean definitionBean;
	
	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(FieldBeanTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Before
	public void before() throws Exception
	{
		fieldBean = new FieldBean();
		definitionBean = new MasterDefinitionBean();
	}
	
	@Test
	public void testSimple() throws Exception {
		Definition d = new Definition();
		d.setShortName("fylkenr");
		d.setName("Fylkesnummer");
		definitionBean.setDefinition(d);
		
		Metadata metadata = new Metadata();
		metadata.setLocation("difi/geo/fylke");
		metadata.setUpdated(System.currentTimeMillis());
		
		// Update first
		fieldBean.update(metadata);
		assertEquals(2, fieldBean.getFields(metadata).size());

		// assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldBean.getUsage("kommuner"));

		// Update second
		fieldBean.update(metadata);
		assertEquals(2, fieldBean.getFields(metadata).size());

		// assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldBean.getUsage("kommuner"));

	}
}
