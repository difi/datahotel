package no.difi.datahotel.util.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DefinitionTest {

	@Test
	public void testSetGet() throws Exception {
		Definition d = new Definition();

		assertNull(d.getName());
		assertNull(d.getShortName());
		assertNull(d.getDescription());

		d.setName("Organisasjonsnummer");
		d.setShortName("orgnr");
		d.setDescription("Identifikator i brreg.");

		assertEquals("Organisasjonsnummer", d.getName());
		assertEquals("orgnr", d.getShortName());
		assertEquals("Identifikator i brreg.", d.getDescription());
	}
	
	@Test
	public void testEquals() throws Exception {
		Definition d1 = new Definition();
		d1.setShortName("orgnr");
		
		assertFalse(d1.equals(null));
		assertFalse(d1.equals("Hello world!"));
		assertTrue(d1.equals(d1));
		
		Definition d2 = new Definition();
		d2.setShortName("orgnr");
		
		assertTrue(d1.equals(d2));
		
		d2.setShortName("country");
		
		assertFalse(d1.equals(d2));
	}
}
