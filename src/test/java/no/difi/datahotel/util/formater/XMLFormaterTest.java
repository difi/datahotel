package no.difi.datahotel.util.formater;

import org.junit.Assert;
import org.junit.Test;

import no.difi.datahotel.BaseTest;

public class XMLFormaterTest extends BaseTest {
	
	@Test
	public void testUnreachable() {
		Assert.assertNull(new XMLFormater.MapConverter().unmarshal(null, null));
	}
}
