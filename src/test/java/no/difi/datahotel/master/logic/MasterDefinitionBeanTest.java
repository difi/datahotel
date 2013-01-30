package no.difi.datahotel.master.logic;

import static no.difi.datahotel.util.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.Filesystem.FOLDER_MASTER_DEFINITION;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.Definition;
import no.difi.datahotel.util.Filesystem;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MasterDefinitionBeanTest extends BaseTest {

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