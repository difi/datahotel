package no.difi.datahotel.logic.login;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BrregOrganizationTest {

	@Test
	public void constructor() throws Exception {
		BrregOrganization org = new BrregOrganization(991825827);
		Assert.assertEquals("difi", org.getShort());

		Thread.sleep(1000);
	}

	@Test
	public void testShortWithWww() throws Exception {
		// Difi
		Assert.assertEquals("difi", new BrregOrganization(991825827).getShort());

		Thread.sleep(1000);
	}

	@Test
	public void testShortWithoutWww() throws Exception {
		// Sogn og Fjordane krets av Norges speiderforbund
		Assert.assertEquals("sfjscout", new BrregOrganization(997086279).getShort());

		Thread.sleep(1000);
	}

	@Test
	public void testShortRegjeringen() throws Exception {
		// Finansdepartementet
		Assert.assertEquals("fin", new BrregOrganization(972417807).getShort());

		Thread.sleep(1000);
	}

	@Test
	public void testShortRegjeringenFAD() throws Exception {
		// Finansdepartementet
		Assert.assertEquals("fad", new BrregOrganization(972417785).getShort());

		Thread.sleep(1000);
	}
	
	@Test
	public void testShortKommune() throws Exception {
		// Oslo kommune
		Assert.assertEquals("oslokommune", new BrregOrganization(958935420).getShort());

		Thread.sleep(1000);
	}

	@Test
	public void testShortNoUrl() throws Exception {
		// Sogndal helselag
		Assert.assertEquals(null, new BrregOrganization(938710120).getShort());

		Thread.sleep(1000);
	}
}
