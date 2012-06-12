package no.difi.datahotel.util.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import no.difi.datahotel.util.model.Definition;
import no.difi.datahotel.util.model.Field;

import org.junit.Test;

public class FieldTest {

	@Test
	public void testSetGet() throws Exception {
		Field f = new Field();

		assertNull(f.getName());
		assertNull(f.getShortName());
		assertNull(f.getContent());
		assertNull(f.getDefinition());
		assertNull(f.getGroupable());
		assertNull(f.getIndexPrimaryKey());
		assertNull(f.getSearchable());

		f.setName("Direktoratet for forvaltning og IKT");
		f.setShortName("difi");
		f.setContent("What goes here?");
		f.setDefinition(new Definition());
		f.setGroupable(true);
		f.setIndexPrimaryKey(false);
		f.setSearchable(false);

		assertEquals("Direktoratet for forvaltning og IKT", f.getName());
		assertEquals("difi", f.getShortName());
		assertEquals("What goes here?", f.getContent());
		assertEquals(new Definition(), f.getDefinition());
		assertEquals(true, f.getGroupable());
		assertEquals(false, f.getIndexPrimaryKey());
		assertEquals(false, f.getSearchable());
	}
	
	@Test
	public void testEquals() throws Exception {
		Field f1 = new Field();
		Field f2 = new Field();

		assertTrue(f1.equals(f1));
		assertFalse(f1.equals(null));
		assertFalse(f1.equals("Hello world!"));
		assertTrue(f1.equals(f2));

		f1.setShortName("miljo");
		f2.setShortName("norge");
		assertFalse(f1.equals(f2));

		f2.setShortName("miljo");
		assertTrue(f1.equals(f2));
	}
	
}
