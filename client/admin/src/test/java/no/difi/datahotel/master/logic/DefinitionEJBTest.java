package no.difi.datahotel.master.logic;

import static no.difi.datahotel.util.shared.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_MASTER_DEFINITION;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefinitionEJBTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(DefinitionEJBTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	private DefinitionEJB definitionEJB;
	private Definition definition;

	@Before
	public void before() throws Exception {
		definitionEJB = new DefinitionEJB();
		definition = null;

		Filesystem.delete(FOLDER_MASTER_DEFINITION);
		Filesystem.delete(FOLDER_SLAVE);
	}

	@Test
	public void testEmpty() throws Exception {
		assertNotNull(definitionEJB.getDefinitions());
		assertEquals(0, definitionEJB.getDefinitions().size());
	}

	@Test
	public void testOne() throws Exception {
		assertEquals(0, definitionEJB.getDefinitions().size());
		assertEquals(null, definitionEJB.getDefinition("value"));
		assertFalse(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value");

		definitionEJB.setDefinition(definition);

		assertEquals(1, definitionEJB.getDefinitions().size());
		assertEquals("Value", definitionEJB.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());
	}

	@Test
	public void testTwo() throws Exception {
		assertEquals(0, definitionEJB.getDefinitions().size());
		assertEquals(null, definitionEJB.getDefinition("value"));
		assertEquals(null, definitionEJB.getDefinition("value2"));
		assertFalse(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value");

		definitionEJB.setDefinition(definition);

		assertEquals(1, definitionEJB.getDefinitions().size());
		assertEquals("Value", definitionEJB.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value2");
		definition.setName("Value2");

		definitionEJB.setDefinition(definition);

		assertEquals(2, definitionEJB.getDefinitions().size());
		assertEquals("Value", definitionEJB.getDefinition("value").getName());
		assertEquals("Value2", definitionEJB.getDefinition("value2").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());
	}

	@Test
	public void testRewrite() throws Exception {
		assertEquals(0, definitionEJB.getDefinitions().size());
		assertEquals(null, definitionEJB.getDefinition("value"));
		assertFalse(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value");

		definitionEJB.setDefinition(definition);

		assertEquals(1, definitionEJB.getDefinitions().size());
		assertEquals("Value", definitionEJB.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());

		definition = new Definition();
		definition.setShortName("value");
		definition.setName("Value2");

		definitionEJB.setDefinition(definition);

		assertEquals(1, definitionEJB.getDefinitions().size());
		assertEquals("Value2", definitionEJB.getDefinition("value").getName());
		assertTrue(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS).exists());
	}
}