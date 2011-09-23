package no.difi.datahotel.util.jersey.model;

import static org.junit.Assert.*;

import org.junit.Test;

import no.difi.datahotel.util.jersey.JSONPObject;

public class JSONPObjectTest {

	@Test
	public void testing() {
		JSONPObject formater = new JSONPObject();
		String result;

		result = formater.format("Hello", null);
		assertTrue(result.startsWith("callback("));

		result = formater.format("Hello", "difi");
		assertTrue(result.startsWith("difi("));
	}
}
