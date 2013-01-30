package no.difi.datahotel.util.formater;

import static org.junit.Assert.*;

import org.junit.Test;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.util.RequestContext;
import no.difi.datahotel.util.formater.JSONPFormater;

public class JSONPFormaterTest extends BaseTest {

	@Test
	public void testing() {
		RequestContext context = new RequestContext();
		
		JSONPFormater formater = new JSONPFormater();
		String result;

		result = formater.format("Hello", context);
		assertTrue(result.startsWith("callback("));

		context.setCallback("difi");
		result = formater.format("Hello", context);
		assertTrue(result.startsWith("difi("));
	}
}
