package no.difi.datahotel.logic;

import static no.difi.datahotel.util.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.Filesystem.FOLDER_MASTER_DEFINITION;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import no.difi.datahotel.logic.MasterDefinitionBean;
import no.difi.datahotel.model.Definition;
import no.difi.datahotel.util.Filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MasterDefinitionBeanTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(MasterDefinitionBeanTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	private MasterDefinitionBean definitionBean;
	private Definition definition;

	@Before
	public void before() throws Exception {
		definitionBean = new MasterDefinitionBean();
		definition = null;

		Filesystem.delete(FOLDER_MASTER_DEFINITION);
		Filesystem.delete(FOLDER_SLAVE);
	}

	@Test
	public void testEmpty() throws Exception {
		assertNotNull(definitionBean.getDefinitions());
		assertEquals(0, definitionBean.getDefinitions().size());
	}

	@Test
	public void testOne() throws Exception {
		assertEquals(0, definitionBean.getDefinitions().size());
		assertEquals(null, definitionBean.getDefinition("value"));
		assertFalse(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value");

		definitionBean.setDefinition(definition);

		assertEquals(1, definitionBean.getDefinitions().size());
		assertEquals("Value", definitionBean.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());
	}

	@Test
	public void testTwo() throws Exception {
		assertEquals(0, definitionBean.getDefinitions().size());
		assertEquals(null, definitionBean.getDefinition("value"));
		assertEquals(null, definitionBean.getDefinition("value2"));
		assertFalse(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value");

		definitionBean.setDefinition(definition);

		assertEquals(1, definitionBean.getDefinitions().size());
		assertEquals("Value", definitionBean.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value2");
		definition.setName("Value2");

		definitionBean.setDefinition(definition);

		assertEquals(2, definitionBean.getDefinitions().size());
		assertEquals("Value", definitionBean.getDefinition("value").getName());
		assertEquals("Value2", definitionBean.getDefinition("value2").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());
	}

	@Test
	public void testRewrite() throws Exception {
		assertEquals(0, definitionBean.getDefinitions().size());
		assertEquals(null, definitionBean.getDefinition("value"));
		assertFalse(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value");

		definitionBean.setDefinition(definition);

		assertEquals(1, definitionBean.getDefinitions().size());
		assertEquals("Value", definitionBean.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value2");

		definitionBean.setDefinition(definition);

		assertEquals(1, definitionBean.getDefinitions().size());
		assertEquals("Value2", definitionBean.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());
	}
}