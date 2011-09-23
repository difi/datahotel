package no.difi.datahotel.util.jersey.model;

import no.difi.datahotel.util.jersey.TextObject;

import static org.junit.Assert.*;
import org.junit.Test;

public class TextObjectTest {

	@Test
	public void testString()
	{
		TextObject formater = new TextObject();
		
		assertEquals("Message", formater.format("Message", null));
	}
	
	@Test
	public void testNull()
	{
		TextObject formater = new TextObject();
		
		assertEquals("null", formater.format("null", null));
	}
}
