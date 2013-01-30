package no.difi.datahotel.util;

import org.junit.Assert;
import org.junit.Test;

import no.difi.datahotel.BaseTest;

/**
 * Testing the simple class. All methods are needed for presentation, but are not triggered. 
 */
public class SimpleErrorTest extends BaseTest {

	@Test
	public void testSimple() {
		SimpleError error = new SimpleError("My message");
		Assert.assertEquals("My message", error.getMessage());
		
		error.setMessage("My new message");
		Assert.assertEquals("My new message", error.getMessage());
	}
}
