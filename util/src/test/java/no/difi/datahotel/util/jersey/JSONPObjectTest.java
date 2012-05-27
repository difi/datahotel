package no.difi.datahotel.util.jersey;

import static org.junit.Assert.*;

import org.junit.Test;

import no.difi.datahotel.util.jersey.JSONPObject;
import no.difi.datahotel.util.jersey.RequestContext;

public class JSONPObjectTest {

	@Test
	public void testing() {
		RequestContext context = new RequestContext();
		
		JSONPObject formater = new JSONPObject();
		String result;

		result = formater.format("Hello", context);
		assertTrue(result.startsWith("callback("));

		context.setCallback("difi");
		result = formater.format("Hello", context);
		assertTrue(result.startsWith("difi("));
	}
}
