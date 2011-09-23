package no.difi.datahotel.logic.model;

import org.junit.Assert;
import org.junit.Test;
import org.scribe.model.Token;

/**
 * Simple test.
 */
public class AccessEntityTest {

	@Test
	public void testConstructor() {
		Token token = new Token("token", "secret");
		AccessEntity access = new AccessEntity(token);

		Assert.assertEquals("token", access.getToken());
		Assert.assertEquals("secret", access.getSecret());
	}

	@Test
	public void testSetGet() {
		AccessEntity access = new AccessEntity();
		access.setId(42L);
		access.setToken("token");
		access.setSecret("secret");

		Assert.assertEquals(42L, access.getId());
		Assert.assertEquals("token", access.getToken());
		Assert.assertEquals("secret", access.getSecret());
	}

	@Test
	public void testGetAccessToken() {
		Token token = new Token("token", "secret");
		AccessEntity access = new AccessEntity(token);

		// Token doesn't override equals.
		Assert.assertEquals(token.getToken(), access.getAccessToken().getToken());
		Assert.assertEquals(token.getSecret(), access.getAccessToken().getSecret());
	}
}
