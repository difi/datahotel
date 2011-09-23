package no.difi.datahotel.logic.login;

import org.junit.Assert;
import org.junit.Test;

/**
 * Simple test.
 */
public class BrukarUserTest {

	@Test
	public void testSetGet()
	{
		BrukarUser user = new BrukarUser();
		
		user.setIdent("my-ident");
		user.setName("Ola Nordmann");
		user.setAdmin(false);
		
		user.setOrganizationName("Norge");
		user.setOrganizationNumber(42L);
		user.setOrganizationShort("norge");
		user.setOrganizationUrl("http://www.norge.no");
		
		Assert.assertEquals("my-ident", user.getIdent());
		Assert.assertEquals("Ola Nordmann", user.getName());
		Assert.assertEquals(false, user.isAdmin());
		
		Assert.assertEquals("Norge", user.getOrganizationName());
		Assert.assertEquals(new Long(42L), user.getOrganizationNumber());
		Assert.assertEquals("norge", user.getOrganizationShort());
		Assert.assertEquals("http://www.norge.no", user.getOrganizationUrl());
	}
	
}
