package no.difi.datahotel.util.formater;

import no.difi.datahotel.util.formater.TextFormater;

import static org.junit.Assert.*;
import org.junit.Test;

public class TextObjectTest {

	@Test
	public void testString()
	{
		TextFormater formater = new TextFormater();
		
		assertEquals("Message", formater.format("Message", null));
	}
	
	@Test
	public void testNull()
	{
		TextFormater formater = new TextFormater();
		
		assertEquals("null", formater.format("null", null));
	}
}
