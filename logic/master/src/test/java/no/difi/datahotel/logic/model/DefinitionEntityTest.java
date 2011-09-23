package no.difi.datahotel.logic.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class DefinitionEntityTest {

	@Test
	public void testConstructor() {
		DefinitionEntity d = new DefinitionEntity();
		assertNull(d.getId());
		assertNull(d.getName());
		assertNull(d.getShortName());
		assertNull(d.getDescription());
		assertNull(d.getFields());
	}

	@Test
	public void testSet() {
		DefinitionEntity d = new DefinitionEntity();
		d.setId(42L);
		d.setName("Java");
		d.setShortName("java");
		d.setDescription("A programming language");
		d.setFields(new ArrayList<FieldEntity>());

		assertEquals(new Long(42), d.getId());
		assertEquals("Java", d.getName());
		assertEquals("java", d.getShortName());
		assertEquals("A programming language", d.getDescription());
		assertNotNull(d.getFields());
		assertEquals(0, d.getFields().size());
	}

	@Test
	public void testEquals() {
		DefinitionEntity d = new DefinitionEntity();
		assertFalse(d.equals(null));
		assertTrue(d.equals(d));
		assertFalse(d.equals("Java!"));

		DefinitionEntity d2 = new DefinitionEntity();
		d.setName("Java");
		d.setShortName("java");
		d2.setName(".NET");
		d2.setShortName("net");

		assertFalse(d.equals(d2));

		assertNotSame(d.hashCode(), d2.hashCode());
	}

}
